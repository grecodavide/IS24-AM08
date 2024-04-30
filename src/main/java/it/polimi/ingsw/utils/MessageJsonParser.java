package it.polimi.ingsw.utils;

import com.google.gson.*;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.actions.*;
import it.polimi.ingsw.network.messages.errors.ErrorMessage;
import it.polimi.ingsw.network.messages.responses.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

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
                return convertClass(ActionMessage.class, messageObject.get("action").getAsString(), messageObject, context);
            } else if (messageObject.has("response")) {
                return convertClass(ResponseMessage.class, messageObject.get("response").getAsString(), messageObject, context);
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

        private Message convertClass(Class<?> mainClass, String type, JsonObject messageObject, JsonDeserializationContext context) {
            Class<?>[] classes = mainClass.getPermittedSubclasses();
            Optional<Class<?>> resultClass = Arrays.stream(classes)
                    .filter(a -> type.equals(a.getSimpleName().replace("Message", "")))
                    .findFirst();
            if (resultClass.isEmpty()) {
                throw new JsonParseException("Value is not found");
            } else {
                return context.deserialize(messageObject, resultClass.get());
            }
        }
    }
}
