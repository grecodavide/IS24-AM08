package it.polimi.ingsw.client.frontend.tui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import it.polimi.ingsw.client.frontend.ClientBoard;
import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.client.frontend.ShownCard;
import it.polimi.ingsw.client.network.NetworkHandlerRMI;
import it.polimi.ingsw.client.network.NetworkHandlerTCP;
import it.polimi.ingsw.exceptions.WrongInputFormatException;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.LeaderboardEntry;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.RequestStatus;

/**
 * Class that handles client game loop from TUI.
 */
public class GraphicalViewTUI extends GraphicalView {
    private final TuiPrinter printer;
    private String lastError;
    private boolean ongoing;
    private List<String> playersWithObjective;
    private final PlayerControls playerControls;
    private final InputHandler inputHandler;
    private final ValidPositions validPositions;
    private final static List<String> helpMessage = List.of(
            "players,     p -> show list of players",
            "write,       w -> write message (add :username to send private text)",
            "chat,        c -> show chat",
            "board,       b -> show your board (or specify a number to show corresponding player's board)",
            "objectives,  o -> show secret and common objectives",
            "hand,        h -> show your hand");

    private List<String> chat;
    private List<String> messages;
    private final static String playerControlPrompt =
            "Type command, or 'help' for a list of available commands.";


    /**
     * Class constructor. Starts the interface and creates all auxiliary objects.
     */
    public GraphicalViewTUI() {
        super();
        this.ongoing = true;
        this.playersWithObjective = new ArrayList<>();
        this.playerControls = new PlayerControls(); // starts disabled
        this.validPositions = new ValidPositions();

        this.chat = new ArrayList<>();
        this.messages = new ArrayList<>();
        try {
            this.printer = new TuiPrinter();
        } catch (Exception e) {
            throw new RuntimeException("Could not access terminal. Quitting now");
        }

        this.inputHandler = new InputHandler(this.printer);

    }

    /**
     * Actually starts the interface, then handles the pre-match.
     */
    private void startInterface() {
        this.printer.clearTerminal();
        this.setNetworkHandler();
        this.printer.clearTerminal();
        this.setMatch();
        new Thread(this::startPlayerControls).start();
    }

    ///////////////////////
    // AUXILIARY METHODS //
    ///////////////////////
    /**
     * Sets the last request's status, eventually notifying all threads of the server's response.
     * 
     * @param status The last request's status
     */
    @Override
    public void setLastRequestStatus(RequestStatus status) {
        synchronized (this.lastRequest) {
            super.setLastRequestStatus(status);
            if (!status.equals(RequestStatus.PENDING)) {
                this.lastRequest.notifyAll();
            }
        }
    }


    /**
     * Waits for the server response after any action was performed. Until the server does not send
     * a response (or an error), the thread is stopped.
     * 
     * @return Whether the last action was successful or not
     */
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


    /**
     * Shows the player's board and hand.
     * 
     * @param board the player's board
     */
    private synchronized void showHand(ClientBoard board) {
        this.printer.printPlayerBoard(this.username, board);
        this.printer.printHandAtBottom(board.getHand());
    }

    /**
     * Asks the user which card he wants to play.
     * 
     * @param board The player's board
     * 
     * @return The chosen card
     */
    private PlayableCard chooseCardFromHand(ClientBoard board) {
        List<PlayableCard> hand = board.getHand();

        this.inputHandler.setPrompt("Choose card to play (1, 2, 3):");
        this.showHand(board);
        String userIn = this.inputHandler.askUser();

        PlayableCard card = null;
        Integer maxValue = hand.size();
        while (card == null) {
            try {
                Integer index = Integer.parseInt(userIn) - 1;
                if (index >= 0 && index < maxValue) {
                    card = hand.get(index);
                } else {
                    throw new NumberFormatException("Number not in range!");
                }
            } catch (NumberFormatException e) {
                this.inputHandler.setPrompt("Not a valid number! try again");
                this.showHand(board);
                userIn = this.inputHandler.askUser();
            }
        }

        return card;
    }


    /**
     * Asks the user which side he wants to play the chosen card.
     * 
     * @param card The card to be placed on the board
     * 
     * @return The chosen side
     */
    private Side chooseCardSide(PlayableCard card) {
        this.printer.clearTerminal();
        this.printer.printPlayableFrontAndBack(card, 0);

        this.inputHandler
                .setPrompt("What side do you want to play the card on? (defaults to front)");
        String userIn = this.inputHandler.askUser();
        return switch (userIn) {
            case "b", "back" -> Side.BACK;
            default -> Side.FRONT;
        };
    }


    /**
     * Asks the user where he wants to play the chosen card.
     * 
     * @param board The current player's board
     * 
     * @return The chosen coordinates
     */
    private Pair<Integer, Integer> chooseCoords(ClientBoard board) {
        Map<Pair<Integer, Integer>, Pair<Integer, Corner>> valids =
                this.validPositions.getValidPlaces();

        Pair<Integer, Integer> coord = null;

        this.inputHandler.setPrompt("Choose where to place card:");
        while (coord == null) {
            this.printer.printValidPlaces(valids);
            this.printer.printPlayerBoard(this.username, board);

            Integer position = -1;
            try {
                position = Integer.valueOf(this.inputHandler.askUser());
            } catch (NumberFormatException e) {
                this.inputHandler.setPrompt("Not a number! Try again");
            }
            if (position != -1) {
                for (Pair<Integer, Integer> cmp : valids.keySet()) {
                    if (valids.get(cmp).first().equals(position)) {
                        coord = cmp;
                    }
                }
                this.inputHandler.setPrompt("Not a valid number! try again");
            }
        }
        return coord;
    }


    /**
     * Gets the player's input while it's not his turn, and then performs the corresponding action.
     */
    private void parsePlayerControl() {
        ClientBoard board = this.clientBoards.get(this.username);
        String userIn, command, argument, player;

        userIn = this.inputHandler.getNextLine();
        this.printer.clearTerminal();

        int splitIndex = userIn.indexOf(" ");
        if (splitIndex == -1) {
            command = userIn;
            argument = "";
        } else {
            command = userIn.substring(0, splitIndex);
            argument = userIn.substring(splitIndex + 1);
        }

        switch (command) {
            case "o", "objectives":
                this.printer.printObjectives(username, board.getColor(), board.getObjective(),
                        this.visibleObjectives);
                break;
            case "h", "hand":
                this.printer.printHand(this.username, board.getColor(), board.getHand());
                break;
            case "b", "board":
                switch (argument) {
                    case "1":
                        player = this.players.get(0);
                        this.printer.printPlayerBoard(player, this.clientBoards.get(player));
                        break;
                    case "2":
                        player = this.players.get(1);
                        this.printer.printPlayerBoard(player, this.clientBoards.get(player));
                        break;
                    case "3":
                        if (this.players.size() > 2) {
                            player = this.players.get(2);
                            this.printer.printPlayerBoard(player, this.clientBoards.get(player));
                        }
                        break;
                    case "4":
                        if (this.players.size() > 3) {
                            player = this.players.get(3);
                            this.printer.printPlayerBoard(player, this.clientBoards.get(player));
                        }
                        break;

                    default:
                        this.printer.printPlayerBoard(this.username,
                                this.clientBoards.get(this.username));
                        break;
                }
                break;
            case "c", "chat":
                this.printer.printChat(this.chat);
                break;
            case "w", "write":
                if (!argument.equals("")) {
                    if (argument.charAt(0) == ':') {
                        splitIndex = argument.indexOf(" ");
                        if (splitIndex != -1) {
                            String text = argument.substring(splitIndex + 1);
                            String recipient = argument.substring(1, splitIndex);
                            if (!argument.equals("")) {
                                this.sendPrivateText(recipient, text);
                            }
                            if (!this.getServerResponse()) {
                                this.messages.add(this.lastError);
                            } else {
                                this.chat.add("(to: " + recipient + "): " + text);
                            }
                        }
                    } else {
                        this.sendBroadcastText(argument);
                        if (!this.getServerResponse()) {
                            this.messages.add(this.lastError);
                        } else {
                            this.chat.add("(you): " + argument);
                        }
                    }

                    this.printer.clearTerminal();
                }
                break;
            case "p", "players":
                this.printer.printSimpleList(this.players, false, true);
                break;

            case "help":
                this.printer.printSimpleList(helpMessage, false, false);
                break;

            default:
                this.printer.printCenteredMessage("Not a known command: '" + command
                        + "'. Type 'help' to show a help message", 0);
                break;
        }

        this.inputHandler.setPrompt(playerControlPrompt);
        this.inputHandler.showPrompt();
    }


    /**
     * Enables the player to use custom commands while it's not his turn.
     */
    private void enablePlayerControls() {
        this.inputHandler.setPrompt(playerControlPrompt);
        this.inputHandler.showPrompt();
        this.playerControls.enable();
    }


    /**
     * Starts the handling of player controls. If enables, polls for user input and as soon as there
     * is something calls {@link GraphicalViewTUI#parsePlayerControl()}. If disabled, stops the
     * thread
     */
    private void startPlayerControls() {
        while (this.ongoing) {
            synchronized (this.playerControls) {
                if (this.playerControls.isEnabled()) {
                    try {
                        if (System.in.available() > 0) {
                            this.parsePlayerControl();
                        } else {
                            Thread.sleep(200);
                        }
                    } catch (InterruptedException e) {
                    } catch (IOException e) {
                        System.err.println("Could not connect! Quitting now...");
                        System.exit(1);
                    }
                } else {
                    try {
                        this.playerControls.wait();
                    } catch (InterruptedException e) {
                    }
                }

            }
        }
    }

    ////////////////////////
    // PRE MATCH METHODS //
    ///////////////////////
    /**
     * Sets the network view, asking the player how he wants to connect (host, port, RMI vs TCP).
     */
    private void setNetworkHandler() {
        String userIn, IPAddr;
        Integer port = null;

        this.inputHandler.setPrompt("Choose IP address:");
        IPAddr = this.inputHandler.askUser();
        this.inputHandler.setPrompt("Choose port:");
        userIn = this.inputHandler.askUser();
        while (port == null) {
            try {
                port = Integer.valueOf(userIn);
            } catch (NumberFormatException e) {
                userIn = this.inputHandler.askUser();
            }
        }

        this.inputHandler.setPrompt("Choose connection type (1 for TCP, 2 for RMI)");
        this.networkHandler = null;
        while (this.networkHandler == null) {
            userIn = this.inputHandler.askUser();
            try {
                switch (userIn) {
                    case "1", "tcp", "TCP":
                        this.setNetworkHandler(new NetworkHandlerTCP(this, IPAddr, port));
                        break;
                    case "2", "rmi", "RMI":
                        this.setNetworkHandler(new NetworkHandlerRMI(this, IPAddr, port));
                        break;
                    default:
                        this.inputHandler.setPrompt(
                                "Not a valid connection type! Choose connection type (1 for TCP, 2 for RMI)");
                        break;
                }
            } catch (Exception e) {
                this.printer.clearTerminal();
                this.printer.printMessage("Could not connect! Try again");
                this.setNetworkHandler();
                return;
            }
        }
    }


    /**
     * Asks the player to choose a username.
     */
    private void chooseUsername() {
        String userIn = "";
        this.inputHandler.setPrompt("Choose username:");
        while (userIn.equals("")) {
            userIn = this.inputHandler.askUser();
            this.inputHandler.setPrompt("Not a valid username! Choose username:");
        }
        super.setUsername(userIn);
    }


    /**
     * Asks the server for a list of available matches and waits for it.
     */
    private void getAvailableMatches() {
        this.lastRequest.setStatus(RequestStatus.PENDING);
        this.networkHandler.getAvailableMatches();

        if (!this.getServerResponse()) {
            this.printer.clearTerminal();
            this.printer.printCenteredMessage("Could not receive availbale matches, try again!", 1);
            this.getAvailableMatches();
            return;
        }
    }


    /**
     * Tries to create a new match, asking the player for match name and max players.
     * 
     * @throws WrongInputFormatException if the max number of players was not specified
     */
    private void createMatch() throws WrongInputFormatException {
        String userIn = this.inputHandler.askUser();
        Integer splitIndex = userIn.indexOf(" ");
        if (splitIndex == -1) {
            throw new WrongInputFormatException("The max players number was not specified!");
        }

        String matchName = userIn.substring(0, splitIndex);
        Integer maxPlayers;
        try {
            maxPlayers = Integer.valueOf(userIn.substring(splitIndex + 1));
        } catch (Exception e) {
            throw new WrongInputFormatException("Bad format for max players number!");
        }

        super.createMatch(matchName, maxPlayers);
    }


    /**
     * Tries to join a match, showing the player a list of matches and their relative index, and
     * asking him which he wants to join.
     * 
     * @param joinables List of matches the player can join
     * 
     * @throws WrongInputFormatException if the player did not specify a valid index
     */
    private void joinMatch(List<AvailableMatch> joinables) throws WrongInputFormatException {
        String userIn = this.inputHandler.askUser();
        Integer matchIndex;
        try {
            matchIndex = Integer.valueOf(userIn) - 1;
        } catch (NumberFormatException e) {
            throw new WrongInputFormatException("You must specify a number!");
        }

        if (matchIndex < 0 || matchIndex >= joinables.size()) {
            throw new WrongInputFormatException("Invalid index!");
        }

        super.joinMatch(joinables.get(matchIndex).name());
    }


    /**
     * Tries to set the match, either creating it or joining an already existing one.
     * 
     * @see GraphicalViewTUI#joinMatch(List)
     * @see GraphicalViewTUI#createMatch()
     */
    private void setMatch() {
        List<AvailableMatch> joinables = new ArrayList<>(), notJoinables = new ArrayList<>();
        this.chooseUsername();

        this.getAvailableMatches();

        String createMatchPrompt = "Type the match name and max players (e.g. MatchTest 2).";
        String joinMatchPrompt = "Type the number corresponding to the match you want to join.";

        this.availableMatches.forEach(match -> {
            if (match.currentPlayers() < match.maxPlayers() || match.isRejoinable()) {
                joinables.add(match);
            } else {
                notJoinables.add(match);
            }
        });
        this.printer.clearTerminal();

        boolean matchSet = false;

        while (!matchSet) {
            try {
                if (this.availableMatches.isEmpty()) {
                    this.inputHandler.setPrompt("No matches available. " + createMatchPrompt);
                    this.createMatch();
                    matchSet = true;
                } else {
                    if (joinables.isEmpty())
                        joinMatchPrompt = "No matches available. " + joinMatchPrompt;

                    this.inputHandler.setPrompt(
                            "Do you want to join a match or (c)reate one? (defaults to join)");
                    String userIn = this.inputHandler.askUser();
                    this.printer.printMatchesLobby(joinables, notJoinables, 0);
                    switch (userIn) {
                        case "c", "C", "create", "Create":
                            this.inputHandler.setPrompt(createMatchPrompt);
                            this.createMatch();
                            matchSet = true;
                            break;

                        default:
                            this.inputHandler.setPrompt(joinMatchPrompt);
                            this.joinMatch(joinables);
                            matchSet = true;
                            break;
                    }
                }
            } catch (WrongInputFormatException e) {
                this.inputHandler.setPrompt(e.getMessage() + "! Try again.");
            }
        }

        if (!this.getServerResponse()) {
            this.printer.clearTerminal();
            this.printer.printCenteredMessage(this.lastError + "! Try again.", 0);
            this.setMatch();
            return;
        }

    }


    /**
     * Show the list of players in the match.
     * 
     * @param someoneUsername Last player who joined the match
     * @param joinedPlayers List of all the other players
     */
    @Override
    public void someoneJoined(String someoneUsername, List<String> joinedPlayers) {
        super.someoneJoined(someoneUsername, joinedPlayers);
        this.printer.clearTerminal();
        this.printer.printWelcomeScreen();
        this.messages.add("Joined players:");
        this.messages.addAll(joinedPlayers);
        this.printer.printListReverse(this.messages);
        this.printer.printPrompt("");
        this.messages.clear();
    }

    ///////////////////
    // MATCH METHODS //
    ///////////////////
    /**
     * Asks the user which side he wants to play the initial card.
     * 
     * @param initialCard The initial card he drew
     */
    @Override
    public void giveInitialCard(InitialCard initialCard) {
        super.giveInitialCard(initialCard);
        this.printer.clearTerminal();
        this.printer.printInitialSideBySide(initialCard, 1);

        this.inputHandler.setPrompt("Choose initial card side (defaults to front)");
        String userIn = this.inputHandler.askUser();
        Side side;
        switch (userIn) {
            case "b", "back":
                side = Side.BACK;
                break;
            default:
                side = Side.FRONT;
                break;
        }

        super.chooseInitialCardSide(side);
        if (!this.getServerResponse()) {
            this.giveInitialCard(initialCard);
        } else {
            this.printer.clearTerminal();
            this.validPositions
                    .addCard(new ShownCard(initialCard, side, new Pair<Integer, Integer>(0, 0)));
        }
    }


    /**
     * Asks the user which secret objective he wants to keep between the random two given to him.
     * 
     * @param secretObjectives the pair of objectives the player has to choose from
     */
    @Override
    public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) {
        super.giveSecretObjectives(secretObjectives);
        this.printer.clearTerminal();
        this.printer.printObjectivePair("Your choices:", secretObjectives, 1);

        this.inputHandler.setPrompt("Choose secret objective (defaults to first):");
        String userIn = this.inputHandler.askUser();
        Objective objective;
        switch (userIn) {
            case "2", "second":
                objective = secretObjectives.second();
                break;
            default:
                objective = secretObjectives.first();
                break;
        }

        super.chooseSecretObjective(objective);
        if (!this.getServerResponse()) {
            this.giveSecretObjectives(secretObjectives);
        }
    }


    /**
     * Adds to the list of player with objectives the last player who chose his secret objective.
     * This is used to determine whether the player currently playing has already chosen secret
     * objective (and so should play a regular turn) or not.
     * 
     * @param someoneUsername the username of the last player who chose secret objective
     */
    @Override
    public void someoneChoseSecretObjective(String someoneUsername) {
        super.someoneChoseSecretObjective(someoneUsername);
        this.playersWithObjective.add(someoneUsername);
    }


    /**
     * Notifies all players (but the current one) that someone is playing their turn.
     */
    @Override
    public void changePlayer() {
        this.printer.clearTerminal();
        ClientBoard board = this.clientBoards.get(this.currentPlayer);

        new Thread(() -> {
            if (board.getPlaced().isEmpty()) { // choosing initial side
                this.printer.printCenteredMessage(this.currentPlayer + " is choosing initial side!",
                        0);
                this.printer.printPrompt("");
            } else if (!this.playersWithObjective.contains(this.currentPlayer)) { // choosing
                                                                                  // objective
                this.printer.printCenteredMessage(
                        this.currentPlayer + " is choosing secret objective!", 0);
                this.printer.printPrompt("");
            } else {
                this.enablePlayerControls();
            }
        }).start();
    }


    /**
     * Ask a player to choose a card to play, the side on which the card should be played, and the
     * coordinates in which the card should be played; finally trying to actually play the card.
     */
    @Override
    public void makeMove() {
        this.playerControls.disable();
        this.printer.clearTerminal();

        if (this.lastRequest.getStatus().equals(RequestStatus.FAILED)) {
            this.messages.add(lastError + " Try again.");
        }
        if (this.lastTurn) {
            this.messages.add("This is the last turn! Play carefully");
        }
        if (!this.messages.isEmpty()) {
            this.printer.printMessages(messages);
        }

        ClientBoard board = this.clientBoards.get(this.username);
        PlayableCard card = this.chooseCardFromHand(board);
        Side side = this.chooseCardSide(card);
        Pair<Integer, Integer> coords = this.chooseCoords(board);
        ShownCard shownCard = new ShownCard(card, side, coords);

        this.printer.clearTerminal();
        this.printer.printPlayerBoard(this.username, board);
        this.printer.printCard(shownCard);

        this.inputHandler.setPrompt("Are you sure? (n to cancel)");
        String userIn = this.inputHandler.askUser();
        if (userIn.equals("n")) {
            this.makeMove();
            return;
        }

        super.playCard(coords, card, side);
        if (!this.getServerResponse()) {
            this.printer.clearTerminal();
            this.printer.clearTerminal();
            this.makeMove();
            return;
        } else {
            this.messages.clear();
            this.validPositions.addCard(shownCard);
        }
    }


    /**
     * Asks the player from where he wants to draw.
     * 
     * @param availableResources All the possible draw sources
     */
    private void makeUserDraw(Map<Symbol, Integer> availableResources) {
        this.printer.clearTerminal();
        DrawSource source = null;
        this.printer.printAvailableResources(availableResources, 0);
        String userIn;
        this.inputHandler.setPrompt("Choose a draw source: ");
        while (source == null) {
            this.printer.printDrawingScreen(decksTopReign, visiblePlayableCards);

            this.inputHandler.setPrompt("Choose draw source:");
            userIn = this.inputHandler.askUser();
            switch (userIn) {
                case "G", "g":
                    source = DrawSource.GOLDS_DECK;
                    break;
                case "R", "r":
                    source = DrawSource.RESOURCES_DECK;
                    break;
                case "1":
                    source = DrawSource.FIRST_VISIBLE;
                    break;
                case "2":
                    source = DrawSource.SECOND_VISIBLE;
                    break;
                case "3":
                    source = DrawSource.THIRD_VISIBLE;
                    break;
                case "4":
                    source = DrawSource.FOURTH_VISIBLE;
                    break;
                default:
                    this.inputHandler.setPrompt("Not a valid source! Try again.");
                    break;
            }
        }
        super.drawCard(source);
        if (!getServerResponse()) {
            this.makeUserDraw(availableResources);
            return;
        }
    }


    /**
     * Notifies all players that someone played a card, and updates the relative player's board.
     * 
     * @param someoneUsername The player that played the card
     * @param coords The chosen coordinates
     * @param card The chosen played card
     * @param side The chosen side
     * @param points The points of that player
     * @param availableResources The resources of that player
     */
    @Override
    public void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords,
            PlayableCard card, Side side, int points, Map<Symbol, Integer> availableResources) {
        super.someonePlayedCard(someoneUsername, coords, card, side, points, availableResources);

        if (this.username.equals(someoneUsername)) {
            this.makeUserDraw(availableResources);
        }
    }


    /**
     * Notifies everyone else that a player left.
     * 
     * @param someoneUsername Player's username
     */
    @Override
    public void someoneQuit(String someoneUsername) {
        this.printer.printCenteredMessage(someoneUsername + " quit!", 0);
    }


    /**
     * Shows whether the current player won or lost.
     * 
     * @param ranking Match ranking
     */
    @Override
    public void matchFinished(List<LeaderboardEntry> ranking) {
        this.printer.clearTerminal();
        this.ongoing = false;
        this.printer.printEndScreen(ranking, this.username);
    }


    /**
     * Sets the last error message to what the server responded.
     * 
     * @param exception The thrown exception
     */
    @Override
    public void notifyError(Exception exception) {
        super.notifyError(exception);
        this.lastError = exception.getMessage();
        if (this.lastError == null) {
            this.lastError = exception.getClass().getName();
        }
    }


    /**
     * Notifies that the match has started.
     */
    @Override
    protected void notifyMatchStarted() {}


    /**
     * Notifies that the player correctly rejoined a match, and makes him play his turn.
     * 
     * @param drawPhase whether the player should draw or play
     */
    @Override
    protected void notifyMatchResumed(boolean drawPhase) {
        new Thread(() -> {

            this.clientBoards.get(this.username).getPlaced()
                    .forEach((turn, shownCard) -> this.validPositions.addCard(
                            new ShownCard(shownCard.card(), shownCard.side(), shownCard.coords())));

            // we resume match only if the game was in progress, so all players chose secret
            // objectives
            this.players.forEach(this.playersWithObjective::add);

            this.printer.clearTerminal();
            if (this.username.equals(this.currentPlayer)) {
                if (drawPhase) {
                    this.makeUserDraw(this.clientBoards.get(this.username).getAvailableResources());
                    // new Thread(() ->
                    // this.makeUserDraw(this.clientBoards.get(this.username).getAvailableResources())).start();;
                } else {
                    // new Thread(this::makeMove).start();
                    this.makeMove();
                }
            } else {
                this.enablePlayerControls();
            }

        }).start();
    }


    /**
     * Adds to the chat a broadcast text.
     * 
     * @param someoneUsername The player who sent the broadcast
     * @param text The sent text
     */
    @Override
    public void someoneSentPrivateText(String someoneUsername, String text) {
        super.someoneSentPrivateText(someoneUsername, text);

        if (!this.username.equals(someoneUsername)) {
            this.chat.add("(" + someoneUsername + "): " + text);
            this.messages.add(someoneUsername + " sent a private text!");
            this.printer.printMessages(this.messages);
            this.inputHandler.showPrompt();
        }
    }


    /**
     * Adds to the chat a private text.
     * 
     * @param someoneUsername The player who sent the private text
     * @param text The sent text
     */
    @Override
    public void someoneSentBroadcastText(String someoneUsername, String text) {
        super.someoneSentBroadcastText(someoneUsername, text);

        if (!this.username.equals(someoneUsername)) {
            this.chat.add("[" + someoneUsername + "]: " + text);
            this.messages.add(someoneUsername + " sent a text!");
            this.printer.printMessages(this.messages);
            this.inputHandler.showPrompt();
        }
    }

    /**
     * Notifies that there has been a connection error. We only care about server crashes, but it
     * could be anything
     */
    @Override
    public void notifyConnectionLost() {
        this.printer.clearTerminal();
        this.printer.printCenteredMessage("Connection Lost!", 0);
        System.exit(1);
    }


    /**
     * Launch the TUI client
     * @param args command line arguments
     */
    public static void main(String[] args) {
        GraphicalViewTUI tui = new GraphicalViewTUI();
        tui.startInterface();
        while (tui.ongoing) {
        }
    }
}
