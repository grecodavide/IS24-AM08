package it.polimi.ingsw.network.tcp;

import org.junit.Test;

import it.polimi.ingsw.server.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public class ServerTCPTest {

    @Test
    public void startMatch() throws RemoteException {
        Integer port = 9999;

        Server server = new Server(12, 12);
        TCPServer tcpServer = new TCPServer(port, server);
    }
}
