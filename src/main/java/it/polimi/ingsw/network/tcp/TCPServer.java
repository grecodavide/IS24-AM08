package it.polimi.ingsw.network.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;

import it.polimi.ingsw.server.Server;

/**
 * ServerTCP
 */
public class TCPServer {
    private ServerSocket serverSocketTCP;
    private Server server;

    public TCPServer(Integer port, Server server) {
        try {
            this.serverSocketTCP = new ServerSocket(port);
            this.server = server;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        while (!this.serverSocketTCP.isClosed()) {
            try (Socket socket = this.serverSocketTCP.accept()) {
                new ListenerThread(socket, this.server).start();
            } catch (IOException e) {
                System.out.println("Failed to accept socket");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Integer port = 9999;

        Server server = null;
        try {
            server = new Server(1234, port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        TCPServer tcpServer = new TCPServer(port, server);
        tcpServer.listen();

    }
}
