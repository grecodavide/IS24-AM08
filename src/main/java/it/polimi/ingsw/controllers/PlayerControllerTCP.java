package it.polimi.ingsw.controllers;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import it.polimi.ingsw.gamemodel.Card;
import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.InitialCard;
import it.polimi.ingsw.gamemodel.Match;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Player;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.network.messages.ActionMessage;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.utils.Pair;

public class PlayerControllerTCP extends PlayerController {
    private Socket socket;
    private ObjectOutputStream outputStream;

    public PlayerControllerTCP(Player player, Match match, Socket socket) {
        super(player, match);
        try {
            this.socket = socket;
            this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            // match.removePlayer(player);
        }
    }

    private void sendMessage(Message msg) {
        try {
            Gson gson = new Gson();
            this.outputStream.writeObject(gson.toJson(msg));
        } catch (Exception e) {
            e.printStackTrace();
            // match.removePlayer(player);
        }
    }

    @Override
    public void matchStarted() {
    }

    @Override
    public void someoneDrewInitialCard(Player someone, InitialCard card) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", someone.getNickname());
        parameters.put("card", card.getId());

        ActionMessage message = new ActionMessage("someoneDrewInitialCard", parameters);
        sendMessage(message);
    }

    @Override
    public void someoneSetInitialSide(Player someone, Side side) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", someone.getNickname());
        parameters.put("side", side.toString()); // TODO: check behavior

        ActionMessage message = new ActionMessage("someoneDrewInitialCard", parameters);
        sendMessage(message);
    }

    @Override
    public void someoneDrewSecretObjective(Player p, Pair<Objective, Objective> objectives) {
    }

    @Override
    public void someoneChoseSecretObjective(Player p, Objective objective) {
    }

    @Override
    public void someonePlayedCard(Player p, Pair<Integer, Integer> coords, PlayableCard card, Side side) {
    }

    @Override
    public void someoneDrewCard(Player p, DrawSource source, Card card) {
    }

    @Override
    public void matchFinished() {
    }

    @Override
    public void drawInitialCard() {
    }

    @Override
    public void chooseInitialCardSide(Side side) {
    }

    @Override
    public void drawSecretObjectives() {
    }

    @Override
    public void chooseSecretObjective(Objective objective) {
    }

    @Override
    public void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) {
    }

    @Override
    public void drawCard(DrawSource source) {
    }

}
