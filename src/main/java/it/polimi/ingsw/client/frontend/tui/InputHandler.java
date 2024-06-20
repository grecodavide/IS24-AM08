package it.polimi.ingsw.client.frontend.tui;

import java.io.IOException;
import java.util.Scanner;

/**
 * Class that handles the prompt and gets the user input
 */
public class InputHandler {
    private final TuiPrinter printer;
    private final Scanner scanner;
    private String prompt;


    /**
     * Class constructor.
     * 
     * @param printer The actual printer
     */
    public InputHandler(TuiPrinter printer) {
        this.printer = printer;
        this.scanner = new Scanner(System.in);
    }


    /**
     * Sets the current prompt, without displaying it.
     * 
     * @param prompt The new value
     */
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }


    /**
     * Gets the next user input.
     * 
     * @return The next user input
     */
    public String getNextLine() {
        return this.scanner.nextLine();
    }


    /**
     * Shows the current prompt.
     */
    public void showPrompt() {
        this.printer.printPrompt(this.prompt);
    }


    /**
     * Flushes the stdin, so that every input from the user before showing the prompt gets discarded.
     */
    private void clearStdin() {
        try {
            System.in.read(new byte[System.in.available()]);
        } catch (IOException e) {
            this.printer.printCenteredMessage("Disconnected! Looks like the game is gone", 0);
        }
    }

    
    /**
     * Flushes stdin, prints prompt and then gets the user input.
     * 
     * @return The user input
     */
    public String askUser() {
        this.clearStdin();
        this.printer.printPrompt(this.prompt);
        String userIn = this.scanner.nextLine();
        this.printer.clearTerminal();
        return userIn;
    }

}
