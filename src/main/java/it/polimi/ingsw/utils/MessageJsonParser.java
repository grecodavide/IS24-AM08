package it.polimi.ingsw.utils;

import com.google.gson.*;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.actions.*;
import it.polimi.ingsw.network.messages.errors.ErrorMessage;
import it.polimi.ingsw.network.messages.responses.MatchFinishedMessage;
import it.polimi.ingsw.network.messages.responses.MatchStartedMessage;
import it.polimi.ingsw.network.messages.responses.SomeoneChoseSecretObjectiveMessage;
import it.polimi.ingsw.network.messages.responses.SomeoneDrewCardMessage;
import it.polimi.ingsw.network.messages.responses.SomeoneDrewInitialCardMessage;
import it.polimi.ingsw.network.messages.responses.SomeoneDrewSecretObjectivesMessage;
import it.polimi.ingsw.network.messages.responses.SomeonePlayedCardMessage;
import it.polimi.ingsw.network.messages.responses.SomeoneSetInitialSideMessage;

import java.lang.reflect.Type;

public class MessageJsonParser {

    Gson messageBuilder;

    public MessageJsonParser() {
        messageBuilder = new GsonBuilder().registerTypeAdapter(Message.class, new MessageTypeAdapter()).create();
    }

    public Gson getMessageBuilder() {
        return messageBuilder;
    }


    public Message toMessage(String json) {
        try {
            return messageBuilder.fromJson(json, Message.class);
        } catch (Exception e) {
            throw new JsonParseException("Error converting to message");
        }
    }

    public String toJson(Message m) {
        return messageBuilder.toJson(m);
    }

    private class MessageTypeAdapter implements JsonDeserializer<Message> {

        @Override
        public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject messageObject = json.getAsJsonObject();

            if (messageObject.has("action")) {
                return convertAction(messageObject.get("action").getAsString(), messageObject, context);
            } else if (messageObject.has("response")) {
                return convertResponse(messageObject.get("response").getAsString(), messageObject, context);
            } else if (messageObject.has("error")) {
                return convertError(messageObject.get("error").getAsString(), messageObject, context);
            } else {
                throw new JsonParseException("Wrong message type");
            }
        }

        private Message convertError(String type, JsonObject messageObject, JsonDeserializationContext context) {
            //TODO better error handling
            return context.deserialize(messageObject, ErrorMessage.class);
        }

        private Message convertAction(String type, JsonObject messageObject, JsonDeserializationContext context) {
            switch (type) {
                case "DrawInitialCard" -> {
                    return context.deserialize(messageObject, DrawInitialCardMessage.class);
                }
                case "ChooseInitialCardSide" -> {
                    return context.deserialize(messageObject, ChooseInitialCardSideMessage.class);
                }
                case "DrawSecretObjectives" -> {
                    return context.deserialize(messageObject, DrawSecretObjectivesMessage.class);
                }
                case "ChooseSecretObjective" -> {
                    return context.deserialize(messageObject, ChooseSecretObjectiveMessage.class);
                }
                case "PlayCard" -> {
                    return context.deserialize(messageObject, PlayCardMessage.class);
                }
                case "DrawCard" -> {
                    return context.deserialize(messageObject, DrawCardMessage.class);
                }
                default -> {
                    throw new JsonParseException("Wrong message action");
                }
            }
        }
    }

    private Message convertResponse(String type, JsonObject messageObject, JsonDeserializationContext context) {
        switch (type) {
            case "MatchFinished" -> {
                return context.deserialize(messageObject, MatchFinishedMessage.class);
            }
            case "MatchStarted" -> {
                return context.deserialize(messageObject, MatchStartedMessage.class);
            }
            case "SomeoneChoseSecretObjective" -> {
                return context.deserialize(messageObject, SomeoneChoseSecretObjectiveMessage.class);
            }
            case "SomeoneDrewCardMessage" -> {
                return context.deserialize(messageObject, SomeoneDrewCardMessage.class);
            }
            case "SomeoneDrewInitialCard" -> {
                return context.deserialize(messageObject, SomeoneDrewInitialCardMessage.class);
            }
            case "SomeoneSetInitialSide" -> {
                return context.deserialize(messageObject, SomeoneSetInitialSideMessage.class);
            }
            case "SomeoneDrewSecretObjectives" -> {
                return context.deserialize(messageObject, SomeoneDrewSecretObjectivesMessage.class);
            }
            case "SomeonePlayedCard" -> {
                return context.deserialize(messageObject, SomeonePlayedCardMessage.class);
            } 
            default -> {
                throw new JsonParseException("Wrong message response");
            }
        }
    }
}
