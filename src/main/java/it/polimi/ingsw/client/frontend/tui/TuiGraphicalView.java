package it.polimi.ingsw.client.frontend.tui;

import java.util.List;
import java.util.Scanner;
import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.client.network.NetworkViewRMI;
import it.polimi.ingsw.client.network.NetworkViewTCP;
import it.polimi.ingsw.utils.LeaderboardEntry;
import it.polimi.ingsw.utils.RequestStatus;

/**
 * TuiGraphicalView
 */

public class TuiGraphicalView extends GraphicalView {
    private final TuiPrinter printer;
    private final Scanner scanner;

    public TuiGraphicalView() {
        try {
            this.printer = new TuiPrinter();
            this.lastRequestStatus = RequestStatus.PENDING;
        } catch (Exception e) {
            throw new RuntimeException("Could not access terminal. Quitting now");
        }

        this.scanner = new Scanner(System.in);
    }

    private void startInterface() {
        new Thread(() -> {
            this.printer.clearTerminal();
            this.setNetwork();
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
        synchronized (this.lastRequestStatus) {
            super.setLastRequestStatus(status);
            if (!status.equals(RequestStatus.PENDING)) {
                this.printer.printCenteredMessage("Status: " + this.lastRequestStatus.toString(), 1);
                this.lastRequestStatus.notifyAll();
            }
        }
    }

    private boolean waitServerResponse() throws InterruptedException {
        this.printer.printCenteredMessage("Waiting for server...", 1);
        synchronized (this.lastRequestStatus) {
            while (this.lastRequestStatus.equals(RequestStatus.PENDING)) {
                this.lastRequestStatus.wait();
            }
        }
        return this.lastRequestStatus.equals(RequestStatus.SUCCESSFUL);
    }

    private void parseMatchDecision(String prompt) {
        String userIn;
        boolean requestSent = false;
        Integer splitIndex;

        while (!requestSent) {
            userIn = this.askUser(prompt);
            splitIndex = userIn.indexOf(" ");
            if (splitIndex == -1) {
                // join
                try {
                    Integer matchIndex = Integer.valueOf(userIn) - 1;
                    requestSent = true;
                    this.joinMatch(this.availableMatches.get(matchIndex).name());
                } catch (NumberFormatException e) {
                    prompt = "Bad format: Not a number! Try again:";
                }
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
        String userIn;

        this.setUsername(this.askUser("Choose username:"));
        this.networkView.getAvailableMatches();
        if (!this.waitServerResponse()) {
            this.setMatch();
            return;
        }

        if (this.availableMatches.size() == 0) {
            userIn = this.askUser("No matches available. Create one by typing match name and max players (e.g. MatchTest 2):");
        } else {
            this.printer.printMatchesLobby(this.availableMatches, 1); // not to be centered!
            userIn = this.askUser("Join a match by typing its number, or create one by typing its name and max players (e.g. MatchTest 2)");
        }
        this.parseMatchDecision(userIn);
        if (!this.waitServerResponse()) {
            this.setMatch();
        } else {
            this.printer.clearTerminal();
            this.printer.printCenteredMessage("Everything went fine!", 1);
        }
    }

    // MATCH METHODS
    @Override
    public void changePlayer() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changePlayer'");
    }

    @Override
    public void makeMove() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'makeMove'");
    }

    @Override
    public void giveLobbyInfo(List<String> playersUsernames) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'giveLobbyInfo'");
    }

    @Override
    protected void notifyMatchStarted() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyMatchStarted'");
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
        TuiGraphicalView tui = new TuiGraphicalView();
        tui.startInterface();
        while (true) {

        }
    }
}
