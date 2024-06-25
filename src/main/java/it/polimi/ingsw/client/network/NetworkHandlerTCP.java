package it.polimi.ingsw.client.network;

import java.io.IOException;
import java.net.Socket;
import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.actions.*;
import it.polimi.ingsw.network.tcp.ClientReceiver;
import it.polimi.ingsw.network.tcp.IOHandler;
import it.polimi.ingsw.utils.Pair;

public class NetworkHandlerTCP extends NetworkHandler {
    private final IOHandler io;
    private final Socket socket;

    public NetworkHandlerTCP(GraphicalView graphicalView, String address, Integer port) throws IOException {
        super(graphicalView, address, port);
        this.socket = new Socket(address, port);
        this.io = new IOHandler(socket);
        new Thread(new ClientReceiver(this, socket)).start();
        connected = true;
        super.startConnectionCheck();
    }

    public void notifyError(Exception exception) {
        this.graphicalView.notifyError(exception);
    }

    public String getUsername() {
        return this.username;
    }

    public IOHandler getIO() {
        return this.io;
    }

    private void sendMessage(Message msg) {
        try {
            this.io.writeMsg(msg);
        } catch (IOException e) {
            // TODO: handle IO
        }
    }

    @Override
    public void getAvailableMatches() {
        this.sendMessage(new GetAvailableMatchesMessage(this.username));
    }

    @Override
    public void createMatch(String matchName, Integer maxPlayers) {
        this.sendMessage(new CreateMatchMessage(this.username, matchName, maxPlayers));
    }

    @Override
    public void joinMatch(String matchName) {
        this.sendMessage(new JoinMatchMessage(this.username, matchName));
    }

    @Override
    public void drawInitialCard() {
        this.sendMessage(new DrawInitialCardMessage(this.username));
    }

    @Override
    public void chooseInitialCardSide(Side side) {
        this.sendMessage(new ChooseInitialCardSideMessage(this.username, side));
    }

    @Override
    public void drawSecretObjectives() {
        this.sendMessage(new DrawSecretObjectivesMessage(this.username));
    }

    @Override
    public void chooseSecretObjective(Objective objective) {
        this.sendMessage(new ChooseSecretObjectiveMessage(this.username, objective.getID()));
    }

    @Override
    public void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        this.sendMessage(new PlayCardMessage(this.username, coords, card.getId(), side));
    }

    @Override
    public void drawCard(DrawSource source) {
        this.sendMessage(new DrawCardMessage(this.username, source));
    }

    @Override
    public void sendBroadcastText(String text) {
        this.sendMessage(new SendBroadcastTextMessage(this.username, text));
    }

    @Override
    public void sendPrivateText(String recipient, String text) {
        this.sendMessage(new SendPrivateTextMessage(this.username, recipient, text));
    }

    @Override
    public void disconnect() {
        connected = false;
    }

    @Override
    public boolean ping() {
        try {
            io.writeMsg("ping");
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
