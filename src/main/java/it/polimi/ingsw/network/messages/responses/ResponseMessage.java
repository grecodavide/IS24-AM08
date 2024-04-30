package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.network.messages.Message;

/**
 * ResponseMessage
 */
public sealed class ResponseMessage extends Message permits AvailableMatchesMessage, MatchFinishedMessage, MatchStartedMessage, PlayerJoinedMessage, PlayerQuitMessage, SomeoneChoseSecretObjectiveMessage, SomeoneDrewCardMessage, SomeoneDrewInitialCardMessage, SomeoneDrewSecretObjectivesMessage, SomeonePlayedCardMessage, SomeoneSentTextMessage, SomeoneSetInitialSideMessage {
    public String username;
    public String response = this.getClass().getSimpleName().replace("Message", "");

    public ResponseMessage(String username) {
        this.username = username;
    }
    
}
