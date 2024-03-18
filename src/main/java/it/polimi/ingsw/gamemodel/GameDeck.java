package it.polimi.ingsw.gamemodel;

import java.util.ArrayList;
import java.util.List;

/*
 * Generic used to create the decks for all the types of cards
 * */
public class GameDeck<T> {
    private int size;
    private List<T> cardsList;

    public GameDeck(int size) {
        this.size = size;
        cardsList = new ArrayList<>();
    }
}
