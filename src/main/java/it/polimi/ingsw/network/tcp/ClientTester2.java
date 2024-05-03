package it.polimi.ingsw.network.tcp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.actions.ActionMessage;
import it.polimi.ingsw.network.messages.actions.DrawInitialCardMessage;
import it.polimi.ingsw.network.messages.actions.GetAvailableMatchesMessage;
import it.polimi.ingsw.network.messages.actions.JoinMatchMessage;
import it.polimi.ingsw.network.messages.responses.AvailableMatchesMessage;
import it.polimi.ingsw.utils.MessageJsonParser;

/**
 * ClientTester2
 */
public class ClientTester2 {

    public static void main(String[] args) throws Exception {

        MessageJsonParser parser = new MessageJsonParser();

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

        ActionMessage getAvailable = new GetAvailableMatchesMessage(username);
        out.writeObject(parser.toJson(getAvailable));

        AvailableMatchesMessage received = (AvailableMatchesMessage) parser.toMessage(in.readObject().toString());

        ActionMessage join = new JoinMatchMessage(username, received.getMatches().get(0).get("name").getAsString());
        out.writeObject(parser.toJson(join));

        Message chooseInitial = new DrawInitialCardMessage(username);
        out.writeObject(parser.toJson(chooseInitial));

        while (client.isConnected()) {
            System.out.println(in.readObject().toString());
        }

        client.close();
    }

}
