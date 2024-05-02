package it.polimi.ingsw.network.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.utils.MessageJsonParser;

/**
 * This class will handle all the IO operations for a certain socket
 * @see ObjectInputStream
 * @see ObjectOutputStream
 * @see MessageJsonParser
 */
public class IOHandler {
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private MessageJsonParser parser;

    /**
     * Class constructor. It takes a {@link Socket} as a parameter to open its
     * {@link ObjectOutputStream} and {@link ObjectInputStream}
     */
    public IOHandler(Socket socket) throws IOException {
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        this.parser = new MessageJsonParser();
    }

    /**
     * Acquires a {@link Message} from the socket's input stream
     * 
     * @return the string representing the message
     * @throws IOException            if the remote communication failed
     * @throws ClassNotFoundException if the class of the received object could not
     *                                be found
     */
    public String readMsg() throws IOException, ClassNotFoundException {
        return this.inputStream.readObject().toString();
    }

    /**
     * Writes a {@link Message} to the socket's output stream
     * 
     * @param msg The (parsed) message to write
     * @throws IOException if the remote communication failed
     */
    public void writeMsg(String msg) throws IOException {
        this.outputStream.writeObject(msg);
    }

    /**
     * Writes a {@link Message} to the socket's output stream
     * 
     * @param msg The (not yet parsed) message to write
     * @throws IOException if the remote communication failed
     */
    public void writeMsg(Message msg) throws IOException {
        this.outputStream.writeObject(this.msgToString(msg));
    }

    /**
     * Converts a {@link Message} to its corresponding Json
     * 
     * @param msg The message to be parsed
     * @return the corresponding Json
     */
    public String msgToString(Message msg) {
        return this.parser.toJson(msg);
    }

    /**
     * Converts a Json string to its corresponding {@link Message}
     * 
     * @param msg The Json to be parsed
     * @return the corresponding object
     */
    public Message stringToMsg(String msg) {
        return this.parser.toMessage(msg);
    }

    /**
     * Closes the input and output streams, if not null
     * @throws IOException if the streams could not be accessed
     */
    public void close() throws IOException {
        if (this.inputStream != null) {
            this.inputStream.close();
        }
        if (this.outputStream != null) {
            this.outputStream.close();
        }
    }
}
