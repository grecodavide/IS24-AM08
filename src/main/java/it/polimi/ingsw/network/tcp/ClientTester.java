package it.polimi.ingsw.network.tcp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import it.polimi.ingsw.network.messages.actions.ActionMessage;
import it.polimi.ingsw.network.messages.actions.CreateMatchMessage;
import it.polimi.ingsw.utils.MessageJsonParser;

/**
 * ClientTester
 */
public class ClientTester {

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


        ActionMessage createMatch = new CreateMatchMessage(username, "pippo", 2);
        out.writeObject(parser.toJson(createMatch));

        System.out.println(in.readObject().toString());

        while (client.isConnected()) {
            try {
                String next = in.readObject().toString();
                if (next != null) {
                    System.out.println(next);
                }
            } catch (Exception e) {
                
            }
        }

        client.close();
    }
}
