package it.polimi.ingsw.network.tcp;

import static org.junit.Assert.assertTrue;

import java.rmi.RemoteException;

import it.polimi.ingsw.server.Server;

public class ServerTCPTest {

    // @Test
    public void startMatch() {
        Integer port = 9999;

        Server server = null;
        try {
            server = new Server(1234, port);
        } catch (RemoteException e) {
            e.printStackTrace();
            assertTrue(false);
        }

        TCPServer tcpServer = new TCPServer(port, server);
        tcpServer.listen();
    }
}
