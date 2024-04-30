package it.polimi.ingsw.network.tcp;

import org.junit.Test;

import it.polimi.ingsw.server.Server;

public class ServerTCPTest {

    @Test
    public void startMatch() {
        Integer port = 9999;

        Server server = new Server();
        TCPServer tcpServer = new TCPServer(port, server);
    }
}
