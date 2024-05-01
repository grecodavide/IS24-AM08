package it.polimi.ingsw.network.messages.actions;

/**
 * It communicates the intention of a player to draw the (2) secret objectives. It can only happen before the first turn of the game.
 * If the action is successful, a {@link it.polimi.ingsw.network.messages.responses.SomeoneDrewSecretObjectivesMessage} response is sent to every client.
 */
public final class DrawSecretObjectivesMessage extends ActionMessage{

    public DrawSecretObjectivesMessage(String username) {
        super(username);
    }
}
