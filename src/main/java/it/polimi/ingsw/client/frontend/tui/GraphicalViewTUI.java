package it.polimi.ingsw.client.frontend.tui;

import java.util.List;
import java.util.Scanner;
import it.polimi.ingsw.client.frontend.ClientBoard;
import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.client.network.NetworkViewRMI;
import it.polimi.ingsw.client.network.NetworkViewTCP;
import it.polimi.ingsw.gamemodel.InitialCard;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.LeaderboardEntry;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.RequestStatus;

/**
 * TuiGraphicalView
 */

public class GraphicalViewTUI extends GraphicalView {
    private final TuiPrinter printer;
    private final Scanner scanner;

    public GraphicalViewTUI() {
        super();
        try {
            this.printer = new TuiPrinter();
        } catch (Exception e) {
            throw new RuntimeException("Could not access terminal. Quitting now");
        }

        this.scanner = new Scanner(System.in);
    }

    private void startInterface() {
        new Thread(() -> {
            this.printer.clearTerminal();
            this.setNetwork();
            this.printer.clearTerminal();
            try {
                this.setMatch();
            } catch (InterruptedException e) {
                // TODO: handle exception
            }

        }).start();
    }

    ///////////////////////
    // AUXILIARY METHODS //
    ///////////////////////
    private String askUser(String prompt) {
        this.printer.printPrompt(prompt);
        String userIn = this.scanner.nextLine();
        this.printer.clearTerminal();
        return userIn;
    }

    @Override
    public void setLastRequestStatus(RequestStatus status) {
        synchronized (this.lastRequest) {
            super.setLastRequestStatus(status);
            if (!status.equals(RequestStatus.PENDING)) {
                this.lastRequest.notifyAll();
            }
        }
    }

    private boolean getServerResponse() {
        this.printer.printCenteredMessage("Waiting for server...", 1);
        try {
            synchronized (this.lastRequest) {
                while (this.lastRequest.getStatus().equals(RequestStatus.PENDING)) {
                    this.lastRequest.wait();
                }
            }
            return this.lastRequest.getStatus().equals(RequestStatus.SUCCESSFUL);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }

    }


    private PlayableCard chooseCardFromHand(ClientBoard board) {
        List<PlayableCard> hand = board.getHand();

        this.printer.printHand(this.username, board.getColor(), hand);
        String userIn = this.askUser("Choose card to play (1, 2, 3)");

        PlayableCard card = null;
        Integer maxValue = hand.size();
        while (card == null) {
            try {
                Integer index = Integer.valueOf(userIn) - 1;
                if (index >= 0 && index < maxValue) {
                    card = hand.get(index);
                }
            } catch (NumberFormatException e) {
                this.printer.clearTerminal();
                this.printer.printHand(this.username, board.getColor(), hand);
                userIn = this.askUser("Not a valid number! try again");
            }
        }

        return card;
    }

    private Side chooseCardSide(PlayableCard card) {
        this.printer.clearTerminal();
        this.printer.printPlayableFrontAndBack(card, 0);
        String userIn = this.askUser("Choose side (b for back, default to front)");
        switch (userIn) {
            case "b":
                return Side.BACK;
            default:
                return Side.FRONT;
        }
    }


    private Pair<Integer, Integer> chooseCoords(ClientBoard board) {
        String prompt = "Choose coordinates for card (e.g. 1,1)";
        String userIn;

        Integer x = null, y = null;
        Integer splitIndex;
        while (x == null || y == null) {
            this.printer.clearTerminal();
            this.printer.printPlayerBoard(this.username, board);
            userIn = this.askUser(prompt);

            splitIndex = userIn.indexOf(",");
            if (splitIndex != -1) {
                try {
                    x = Integer.valueOf(userIn.substring(0, splitIndex));
                } catch (NumberFormatException e) {
                    prompt = "X coordinate is not a number! Try again.";
                }

                try {
                    y = Integer.valueOf(userIn.substring(splitIndex + 1, userIn.length()));
                } catch (NumberFormatException e) {
                    prompt = "Y coordinate is not a number! Try again.";
                }
            } else {
                prompt = "Y coordinate not specified! Try again.";
            }
        }

        return new Pair<Integer, Integer>(x, y);
    }



    // TODO: show again list of availableMatches
    private void parseMatchDecision(String prompt) {
        String userIn;
        boolean requestSent = false;
        Integer splitIndex;

        while (!requestSent) {
            userIn = this.askUser(prompt);
            splitIndex = userIn.indexOf(" ");
            if (splitIndex == -1) {
                // join
                requestSent = true;
                this.joinMatch(userIn);
            } else {
                // create
                String matchName = userIn.substring(0, splitIndex);
                Integer maxPlayers;
                try {
                    maxPlayers = Integer.valueOf(userIn.substring(splitIndex + 1, userIn.length()));
                    requestSent = true;
                    this.createMatch(matchName, maxPlayers);
                } catch (NumberFormatException e) {
                    prompt = "Bad format: max players was not a number! Try again:";
                }
            }
        }
    }

    ////////////////////////
    // PRE MATCH METHODS //
    ///////////////////////
    private void setNetwork() {
        String userIn, IPAddr;
        Integer port = null;

        IPAddr = this.askUser("Choose IP address:");
        userIn = this.askUser("Choose port:");
        while (port == null) {
            try {
                port = Integer.valueOf(userIn);
            } catch (NumberFormatException e) {
                userIn = this.askUser("Not a number. Choose a port:");
            }
        }

        userIn = this.askUser("Choose connection type (1 for TCP, 2 for RMI)");

        this.networkView = null;
        while (this.networkView == null) {
            try {
                switch (userIn) {
                    case "1", "tcp", "TCP":
                        this.setNetworkInterface(new NetworkViewTCP(this, IPAddr, port));
                        break;
                    case "2", "rmi", "RMI":
                        this.setNetworkInterface(new NetworkViewRMI(this, IPAddr, port));
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
            }
        }
    }


    private void setMatch() throws InterruptedException {
        String prompt;

        this.setUsername(this.askUser("Choose username:"));

        this.lastRequest.setStatus(RequestStatus.PENDING);
        this.networkView.getAvailableMatches();
        if (!this.getServerResponse()) {
            this.printer.clearTerminal();
            this.printer.printCenteredMessage("Could not receive availbale matches, try again!", 1);
            this.setMatch();
            return;
        }

        this.printer.clearTerminal();
        if (this.availableMatches.size() == 0) {
            prompt = "No matches available. Create one by typing match name and max players (e.g. MatchTest 2):";
        } else {
            this.printer.printMatchesLobby(this.availableMatches, 1); // not to be centered!
            prompt = "Join a match by typing its name, or create one by typing its name and max players (e.g. MatchTest 2)";
        }
        this.parseMatchDecision(prompt);

        if (!this.getServerResponse()) {
            this.printer.clearTerminal();
            this.printer.printCenteredMessage("Something went wrong.. Try again!", 1);
            this.setMatch();
        } else {
            this.printer.clearTerminal();
            this.printer.printCenteredMessage("Everything went fine!", 1);
        }
    }

    ///////////////////
    // MATCH METHODS //
    ///////////////////
    @Override
    public void giveInitialCard(InitialCard initialCard) {
        super.giveInitialCard(initialCard);
        this.printer.clearTerminal();
        this.printer.printInitialSideBySide(initialCard, 1);
        String userIn = this.askUser("Choose side for initial card (b for back, defaults to front):");
        Side side;
        switch (userIn) {
            case "b":
                side = Side.BACK;
                break;
            default:
                side = Side.FRONT;
                break;
        }

        super.chooseInitialCardSide(side);
        // if (!this.getServerResponse()) {
        //     this.giveInitialCard(initialCard);
        // }
    }

    @Override
    public void someoneSetInitialSide(String someoneUsername, Side side) {
        this.printer.clearTerminal();
        if (this.username.equals(someoneUsername)) {
            this.printer.printPlayerBoard(this.username, this.clientBoards.get(this.username));
            this.printer.printMessage("Correctly played card! waiting for others to choose theirs");
        }
        super.someoneSetInitialSide(someoneUsername, side);
    }

    @Override
    public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) {
        super.giveSecretObjectives(secretObjectives);
        this.printer.clearTerminal();
        this.printer.printObjectivePair("Your choices:", secretObjectives, 1);
        String userIn = this.askUser("Choose secret objective (2 for second, defaults to first)");
        Objective objective;
        switch (userIn) {
            case "2":
                objective = secretObjectives.second();
                break;
            default:
                objective = secretObjectives.first();
                break;
        }

        super.chooseSecretObjective(objective);
        // if (!this.getServerResponse()) {
        //     this.giveSecretObjectives(secretObjectives);
        // }
    }

    @Override
    public void changePlayer() {
        this.printer.printPlayerBoard(this.currentPlayer, this.clientBoards.get(this.currentPlayer));
    }

    @Override
    public void makeMove() {
        this.printer.clearTerminal();
        ClientBoard board = this.clientBoards.get(this.username);
        PlayableCard card = this.chooseCardFromHand(board);
        Side side = this.chooseCardSide(card);
        Pair<Integer, Integer> coords = this.chooseCoords(board);

        super.playCard(coords, card, side);
        // if (!this.getServerResponse()) {
        //     this.makeMove();
        // }
    }

    @Override
    public void giveLobbyInfo(List<String> playersUsernames) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'giveLobbyInfo'");
    }

    @Override
    protected void notifyMatchStarted() {
        this.printer.printCenteredMessage("Match started!", 1);
    }

    @Override
    public void notifyLastTurn() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyLastTurn'");
    }

    @Override
    public void someoneQuit(String someoneUsername) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'someoneQuit'");
    }

    @Override
    public void matchFinished(List<LeaderboardEntry> ranking) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'matchFinished'");
    }

    public static void main(String[] args) {
        GraphicalViewTUI tui = new GraphicalViewTUI();
        tui.startInterface();
        while (true) {

        }
    }
}
