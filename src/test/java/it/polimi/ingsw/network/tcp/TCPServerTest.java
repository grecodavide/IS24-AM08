package it.polimi.ingsw.network.tcp;

import static org.junit.Assert.assertTrue;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.junit.Test;

import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.utils.MessageJsonParser;;

/**
 * TCPServerTest
 */
public class TCPServerTest {
    private Integer port = 9998;
    private String host = "localhost";

    private MessageJsonParser parser;

    private TCPServer server;

    private Socket a;
    private ObjectOutputStream aOut;
    private ObjectInputStream aIn;
    private Socket b;
    private ObjectInputStream bIn;
    private ObjectOutputStream bOut;

    @Test
    public void testMessage() {
        Server server = new Server();

        try {
            this.parser = new MessageJsonParser();

            this.server = new TCPServer(port, server);

            this.a = new Socket(host, port);
            this.b = new Socket(host, port);


            this.bOut = new ObjectOutputStream(this.b.getOutputStream());
            this.bIn = new ObjectInputStream(this.b.getInputStream());
            this.aOut = new ObjectOutputStream(this.a.getOutputStream());
            System.out.println("Sockets created");

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

}
