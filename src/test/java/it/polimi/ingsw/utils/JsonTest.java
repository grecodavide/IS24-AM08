package it.polimi.ingsw.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.actions.DrawCardMessage;
import it.polimi.ingsw.network.messages.actions.DrawInitialCardMessage;
import it.polimi.ingsw.network.messages.actions.PlayCardMessage;
import it.polimi.ingsw.network.messages.responses.SomeoneDrewSecretObjectivesMessage;

public class JsonTest {
    @Test
    public void parseDrawInitialCard() {
        DrawInitialCardMessage m1 = new DrawInitialCardMessage("Oingo");
        // Serialize
        MessageJsonParser parser = new MessageJsonParser();
        String m1json = parser.toJson(m1);
        System.out.println(m1json);
        // Deserialize
        Message convertedMessage = parser.toMessage(m1json);
        assertTrue(convertedMessage instanceof DrawInitialCardMessage);
        assertEquals("Oingo", ((DrawInitialCardMessage) convertedMessage).getUsername());
    }

    @Test
    public void parsePlayCard() {
        PlayCardMessage m1 = new PlayCardMessage("Oingo", new Pair<>(0, 0), 12, Side.FRONT);
        // Serialize
        MessageJsonParser parser = new MessageJsonParser();
        String m1json = parser.toJson(m1);
        System.out.println(m1json);
        Message convertedMessage = parser.toMessage(m1json);
        assertTrue(convertedMessage instanceof PlayCardMessage);
        PlayCardMessage pm = (PlayCardMessage) convertedMessage;
        assertEquals("Oingo",  pm.getUsername());
        assertEquals(Side.FRONT, pm.getSide());
        assertEquals(Integer.valueOf(0), pm.getX());
        assertEquals(Integer.valueOf(0), pm.getY());
        assertEquals(Integer.valueOf(12), pm.getCardID());
    }

    @Test
    public void parseDrawCard() {
        DrawCardMessage m1 = new DrawCardMessage("Oingo", DrawSource.GOLDS_DECK);
        // Serialize
        MessageJsonParser parser = new MessageJsonParser();
        String m1json = parser.toJson(m1);
        System.out.println(m1json);
        // Deserialize
        Message convertedMessage = parser.toMessage(m1json);
        assertTrue(convertedMessage instanceof DrawCardMessage);
        assertEquals(DrawSource.GOLDS_DECK, ((DrawCardMessage) convertedMessage).getSource());
    }

    @Test
    public void parseSomeoneDrawSecretObjectives() {
        SomeoneDrewSecretObjectivesMessage m1 = new SomeoneDrewSecretObjectivesMessage("Boingo", new Pair<>(12, 11));
        // Serialize
        MessageJsonParser parser = new MessageJsonParser();
        String m1json = parser.toJson(m1);
        System.out.println(m1json);
        // Deserialize
        Message convertedMessage = parser.toMessage(m1json);
        assertTrue(convertedMessage instanceof SomeoneDrewSecretObjectivesMessage);
        assertEquals(Integer.valueOf(12), ((SomeoneDrewSecretObjectivesMessage) convertedMessage).getFirstID());
    }

    @Test
    public void wrongJson() {
        String json = "{";
        MessageJsonParser parser = new MessageJsonParser();
        try {
            parser.toMessage(json);
            fail("Exception not thrown");
        } catch (JsonParseException e) {
            // Good
        } catch (Exception e) {
            fail("Wrong exception thrown");
        }

        json = "{\"response\": \"DrawInitialCard\"}";
        try {
            parser.toMessage(json);
            fail("Exception not thrown");
        } catch (JsonParseException e) {
            // Good
        } catch (Exception e) {
            fail("Wrong exception thrown");
        }

        json = "{\"response\": \"Lol\"}";
        try {
            parser.toMessage(json);
            fail("Exception not thrown");
        } catch (JsonParseException e) {
            // Good
        } catch (Exception e) {
            fail("Wrong exception thrown");
        }
    }
}
