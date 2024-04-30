package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.network.messages.Message;

/**
 * ResponseMessage
 */
public sealed class ResponseMessage extends Message permits AvailableMatchesMessage, MatchFinishedMessage, MatchStartedMessage, PlayerJoinedMessage, PlayerQuitMessage, SomeoneChoseSecretObjectiveMessage, SomeoneDrewCardMessage, SomeoneDrewInitialCardMessage, SomeoneDrewSecretObjectivesMessage, SomeonePlayedCardMessage, SomeoneSentTextMessage, SomeoneSetInitialSideMessage {
    private final String username;
    private final String response = this.getClass().getSimpleName().replace("Message", "");

    public String getUsername() {
        return username;
    }

    public String getResponse() {
        return response;
    }

    public ResponseMessage(String username) {
        this.username = username;
    }
    
}
