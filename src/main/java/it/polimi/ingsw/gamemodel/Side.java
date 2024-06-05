package it.polimi.ingsw.gamemodel;

/**
 * Used to know which of the two faces of a card we want to use
 */
public enum Side {
    FRONT("front"),
    BACK("back");

    private String string;

    Side(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    public Side fromString(String cmp) {
        for (Side side : Side.values()) {
            if (side.toString().equals(cmp)) {
                return side;
            }
        }
        return null;
    }
}
