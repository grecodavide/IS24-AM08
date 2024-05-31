package it.polimi.ingsw.network.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import it.polimi.ingsw.gamemodel.Match;
import it.polimi.ingsw.server.Server;

/**
 * Class containing the {@link ServerSocket}. This will just accept sockets and
 * start the {@link ClientListener} with it
 */
public class TCPServer {
    private ServerSocket serverSocketTCP;
    private Server server;

    /**
     * Class constructor. It will open a {@link ServerSocket} on the specified port
     *
     * @param port   the port on which the server should be started
     * @param server the {@link Server} object that contains all the {@link Match}
     *               objects
     */
    public TCPServer(Integer port, Server server) {
        try {
            this.serverSocketTCP = new ServerSocket(port);
            this.server = server;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Main loop. Until the {@link ServerSocket} is not closed, it will listen for
     * any {@link Socket} that tries to connect and accept them. Finally, it will
     * start a new {@link ClientListener} with it
     */
    public void listen() {
        while (!this.serverSocketTCP.isClosed()) {
            try {
                Socket socket = this.serverSocketTCP.accept();
                new ClientListener(socket, server).start();
            } catch (IOException e) {
                System.out.println("Failed to accept socket");
                e.printStackTrace();
            }
        }
    }
}
