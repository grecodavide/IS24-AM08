package it.polimi.ingsw.utils;

import com.google.gson.*;
import it.polimi.ingsw.exceptions.InvalidResourceException;
import it.polimi.ingsw.gamemodel.PositionRequirement;
import it.polimi.ingsw.gamemodel.QuantityRequirement;
import it.polimi.ingsw.gamemodel.Requirement;
import it.polimi.ingsw.gamemodel.Symbol;

import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Parser of Cards, implements a custom parser for Requirement
 */
public class CardJsonParser {
    Gson cardBuilder = new GsonBuilder().registerTypeAdapter(Requirement.class, (new CardTypeAdapter())).setPrettyPrinting().create();

    /**
     * Returns a Gson builder with pretty print and custom Requirement deserializer
     *
     * @return
     */
    public Gson getCardBuilder() {
        return cardBuilder;
    }

    private class CardTypeAdapter implements JsonDeserializer<Requirement> {
        @Override
        public Requirement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject requirementObject = json.getAsJsonObject();
            EnumSet<Symbol> symbols = Symbol.getBasicResources();
            // Check the reqs property
            if (requirementObject.has("reqs")) {
                JsonObject reqObject = requirementObject.get("reqs").getAsJsonObject();
                // If reqs has pairs inside, then it's a PositionRequirement
                if (reqObject.has("Pair[first=0, second=0]")) {
                    try {
                        // Manually create a Positional Requirement
                        Map<Pair<Integer, Integer>, Symbol> reqmap = decodeMap(reqObject);
                        return new PositionRequirement(reqmap);
                    } catch (InvalidResourceException e) {
                        throw new RuntimeException(e);
                    }
                    // If reqs has symbols inside, then it's a QuantityRequirement
                } else if (symbols.stream().anyMatch(s -> reqObject.has(s.toString()))) {
                    return context.deserialize(requirementObject, QuantityRequirement.class);
                } else {
                    throw new JsonParseException(json.toString());
                }
            } else {
                throw new JsonParseException(json.toString());
            }
        }

        /**
         * Deserializes pair object
         *
         * @param pair string of the encoded pair
         * @return The decoded Pair object
         */
        private Pair<Integer, Integer> decodePair(String pair) {
            String values = pair.replace("Pair[first=", "").replace(" second=", "").replace("]", "");
            String[] a = values.split(",");
            return new Pair<>(Integer.parseInt(a[0]), Integer.parseInt(a[1]));
        }

        /**
         * Deserializes the map containing the positional requirement
         *
         * @param j
         * @return
         */
        private Map<Pair<Integer, Integer>, Symbol> decodeMap(JsonObject j) {
            Map<Pair<Integer, Integer>, Symbol> map = new HashMap<>();
            Map<String, JsonElement> jsonmap = j.asMap();
            for (String pairstring : jsonmap.keySet()) {
                map.put(decodePair(pairstring), Symbol.valueOf(jsonmap.get(pairstring).getAsString()));
            }
            return map;
        }

    }
}
