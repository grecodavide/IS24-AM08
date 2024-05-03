package it.polimi.ingsw.network.tcp;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import it.polimi.ingsw.exceptions.ChosenMatchException;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.actions.CreateMatchMessage;
import it.polimi.ingsw.network.messages.actions.GetAvailableMatchesMessage;
import it.polimi.ingsw.network.messages.actions.JoinMatchMessage;
import it.polimi.ingsw.network.messages.errors.ErrorMessage;
import it.polimi.ingsw.network.messages.responses.AvailableMatchesMessage;
import it.polimi.ingsw.network.messages.responses.SomeoneJoinedMessage;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.utils.MessageJsonParser;

public class ServerTCPTest {
    MessageJsonParser messageParser;
    Integer port;
    Server server;
    List<Socket> sockets;

    public ServerTCPTest() throws RemoteException {
        messageParser = new MessageJsonParser();
        port = 9999;
        server = new Server(12, port);
        sockets = new ArrayList<>();
    }

    @Test
    public void startMatch() throws RemoteException, IOException, ClassNotFoundException {
        String matchCreatorUsername = "Davide";
        String matchName = "LelloPisello";
        String matchJoineeUsername = "Luca";
        Integer maxPlayers = 3;

        TCPServer tcpServer = new TCPServer(port, server);
        new Thread(() -> {
            tcpServer.listen();
        }).start();

        String expected = messageParser.toJson(new SomeoneJoinedMessage(matchCreatorUsername, 1, maxPlayers));
        this.testCreation(port, matchCreatorUsername, matchName, maxPlayers, expected);
        this.testJoin(port, matchJoineeUsername, matchName, maxPlayers);
        
        expected = messageParser.toJson(new ErrorMessage("A match with the chosen name already exists", ChosenMatchException.class.getName()));
        this.testCreation(port, matchCreatorUsername + "v2", matchName, maxPlayers, expected);

        this.close();
    }

    public void testCreation(Integer port, String matchCreatorUsername, String matchName, Integer maxPlayers, String expected)
            throws IOException, ClassNotFoundException {
        Socket matchCreator = new Socket("localhost", port);
        IOHandler io = new IOHandler(matchCreator);
        this.sockets.add(matchCreator);

        Message createMatch = new CreateMatchMessage(matchCreatorUsername, matchName, maxPlayers);
        io.writeMsg(createMatch);

        String response = io.readMsg();

        assertEquals(expected, response);

    }

    public void testJoin(Integer port, String matchJoineeUsername, String matchName, Integer maxPlayers)
            throws IOException, ClassNotFoundException {
        Socket matchJoinee = new Socket("localhost", port);
        IOHandler io = new IOHandler(matchJoinee);
        this.sockets.add(matchJoinee);


        // ask for matches
        Message getAvailable = new GetAvailableMatchesMessage(matchJoineeUsername);
        io.writeMsg(getAvailable);

        // test matches response
        String availableExpected = messageParser.toJson(new AvailableMatchesMessage(server.getJoinableMatchesMap()));
        String availableResponse = io.readMsg();
        assertEquals(availableExpected, availableResponse);

        // ask to join match
        Message join = new JoinMatchMessage(matchJoineeUsername, matchName);
        io.writeMsg(join);

        // test join response
        String joinedExpected = messageParser.toJson(new SomeoneJoinedMessage(matchJoineeUsername, 2, maxPlayers));
        String joinedResponse = io.readMsg();
        assertEquals(joinedExpected, joinedResponse);
    }

    private void close() throws IOException {
        for (Socket socket : this.sockets) {
            socket.close();
        }
    }
}
