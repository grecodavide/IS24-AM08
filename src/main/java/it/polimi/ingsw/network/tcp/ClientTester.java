package it.polimi.ingsw.network.tcp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.network.messages.actions.ActionMessage;
import it.polimi.ingsw.network.messages.actions.DrawCardMessage;
import it.polimi.ingsw.utils.MessageJsonParser;

/**
 * ClientTester
 */
public class ClientTester {

    public static void main(String[] args) throws Exception {

        // ##################
        // ## if using cli ##
        // ##################
        Integer port = Integer.valueOf(args[0]);
        String username = args[1];


        // // ##########
        // // ## else ##
        // // ##########
        // Scanner scanner = new Scanner(System.in);
        // Integer port = Integer.valueOf(scanner.nextLine());
        // String username = scanner.nextLine();

        Socket client = new Socket("localhost", port);
        ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(client.getInputStream());

        ActionMessage draw = new DrawCardMessage(username, DrawSource.GOLDS_DECK);
        MessageJsonParser parser = new MessageJsonParser();

        out.writeObject(username);

        out.writeObject(parser.toJson(draw).toString());

        while (client.isConnected()) {
            Object msg = in.readObject();
            System.out.println(msg);
        }

        client.close();

    }
}
