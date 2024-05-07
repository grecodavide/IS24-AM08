package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.network.messages.Message;

/**
 * Messages sent from the server to the clients to update them about another
 * user's move or to the consequence of their action
 */
public sealed class ResponseMessage extends Message permits AvailableMatchesMessage, MatchFinishedMessage, MatchStartedMessage,
        SomeoneJoinedMessage, SomeoneQuitMessage, SomeoneChoseSecretObjectiveMessage, SomeoneDrewCardMessage, SomeoneDrewInitialCardMessage,
        SomeoneDrewSecretObjectivesMessage, SomeonePlayedCardMessage, SomeoneSetInitialSideMessage,
        SomeoneSentBroadcastTextMessage, SomeoneSentPrivateTextMessage {
    private final String username;
    private final String response = this.getClass().getSimpleName().replace("Message", "");

    /**
     * @return username of the user that did the action
     *         null if not specified by the protocol
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return repsonse type
     */
    public String getResponse() {
        return response;
    }

    public ResponseMessage(String username) {
        this.username = username;
    }

}
