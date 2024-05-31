package it.polimi.ingsw.client.frontend.tui;

import java.io.IOException;
import java.util.Scanner;

/**
 * InputHandler
 */

public class InputHandler {
    private final TuiPrinter printer;
    private final Scanner scanner;
    private String prompt;

    public InputHandler(TuiPrinter printer) {
        this.printer = printer;
        this.scanner = new Scanner(System.in);
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getNextLine() {
        return this.scanner.nextLine();
    }

    public void showPrompt() {
        this.printer.printPrompt(this.prompt);
    }

    private void clearStdin() {
        try {
            System.in.read(new byte[System.in.available()]);
        } catch (IOException e) {
            this.printer.printCenteredMessage("Disconnected! Looks like the game is gone", 0);
        }
    }

    public String askUser() {
        this.clearStdin();
        this.printer.printPrompt(this.prompt);
        String userIn = this.scanner.nextLine();
        this.printer.clearTerminal();
        return userIn;
    }

}
