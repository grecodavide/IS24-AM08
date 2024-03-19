package it.polimi.ingsw.gamemodel;

import java.util.ArrayList;
import java.util.List;

/*
* Generic used to create the decks for all the types of cards
*/
public class GameDeck<T> {
    private int size;
    private List<T> cardsList;

    /**
    * Constructor of the class, which will initialize the deck empty
    * @param size initial size of the deck
    */
    public GameDeck(int size) {
        this.size = size;
        cardsList = new ArrayList<>();
    }
}
