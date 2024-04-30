package it.polimi.ingsw.network.messages.actions;

/**
 * It communicates the intention of a player to draw the initial card. It can only happen before the first turn of the game.
 * - If the action is successful, a {@link it.polimi.ingsw.network.messages.responses.SomeoneDrewInitialCardMessage} response is sent to every client.
 */
public final class DrawInitialCardMessage extends ActionMessage {
    public DrawInitialCardMessage(String username) {
        super(username);
    }

}
