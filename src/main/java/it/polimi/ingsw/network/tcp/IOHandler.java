package it.polimi.ingsw.network.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.utils.MessageJsonParser;

/**
 * IOHandler
 */
public class IOHandler {
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private MessageJsonParser parser;

    public IOHandler(Socket socket) throws IOException {
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        this.parser = new MessageJsonParser();
    }

    public String readMsg() throws IOException, ClassNotFoundException {
        return this.inputStream.readObject().toString();
    }

    public void writeMsg (String msg) throws IOException {
        this.outputStream.writeObject(msg);
    }

    public void writeMsg (Message msg) throws IOException {
        this.outputStream.writeObject(this.msgToString(msg));
    }

    public String msgToString(Message msg) {
        return this.parser.toJson(msg);
    }

    public Message stringToMsg(String msg) {
        return this.parser.toMessage(msg);
    }


    public void close() throws IOException {
        if (this.inputStream != null) {
            this.inputStream.close();
        }
        if (this.outputStream != null) {
            this.outputStream.close();
        }
    }
}
