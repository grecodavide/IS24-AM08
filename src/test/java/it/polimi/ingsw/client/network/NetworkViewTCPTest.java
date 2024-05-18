package it.polimi.ingsw.client.network;

import static org.junit.Assert.assertTrue;
import java.net.Socket;
import org.junit.Test;
import it.polimi.ingsw.client.frontend.tui.TuiGraphicalView;
import it.polimi.ingsw.server.Server;

/**
 * NetworkViewTCPTest
 */
// WIP
public class NetworkViewTCPTest {
    private final Integer portTCP = 9999;
    private NetworkViewTCP viewPlayer1;
    private NetworkViewTCP viewPlayer2;
    private String player1 = "Player1";
    private String player2 = "Player2";

    public NetworkViewTCPTest() {
        try {
            Server server = new Server(1212, portTCP);
            server.startTCPServer();
        } catch (Exception e) {
            System.out.println("Could not start server");
            assertTrue(false);
        }
    }

    @Test
    public void testSend() {
        try {
            TuiGraphicalView plyer1View = new TuiGraphicalView();
            viewPlayer1 = new NetworkViewTCP(plyer1View, new Socket("localhost", portTCP));
            viewPlayer1.setUsername(player1);
            viewPlayer1.createMatch("test", 2);

            TuiGraphicalView plyer2View = new TuiGraphicalView();
            viewPlayer2 = new NetworkViewTCP(plyer2View, new Socket("localhost", portTCP));
            viewPlayer2.setUsername(player2);
            viewPlayer2.joinMatch("test");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

}
