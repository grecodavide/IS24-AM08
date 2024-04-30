package it.polimi.ingsw.network.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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

    public static void main(String[] args) throws Exception {
        // ##################
        // ## if using cli ##
        // ##################
        Integer port = Integer.valueOf(args[0]);


        // // ##########
        // // ## else ##
        // // ##########
        // Scanner scanner = new Scanner(System.in);
        // Integer port = Integer.valueOf(scanner.nextLine());

        Server tmp = new Server();
        TCPServer server = new TCPServer(port, tmp);

        server.listen();
    }

}
