package it.polimi.ingsw.network.messages.actions;

/**
 * The client asks for an updated version of the lobby;
 * The server returns an {@link it.polimi.ingsw.network.messages.responses.AvailableMatchesMessage} response.
 */
public final class GetAvailableMatchesMessage extends ActionMessage {
    public GetAvailableMatchesMessage(String username) {
        super(username);
    }
}
