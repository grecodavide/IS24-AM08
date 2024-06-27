package it.polimi.ingsw.gamemodel;

/**
 * Represents a card side.
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

    /**
     * Return a instance of this enum from its corresponding string representation.
     *
     * @param sideString The side represented as a string
     * @return The side instance
     */
    public Side fromString(String sideString) {
        for (Side side : Side.values()) {
            if (side.toString().equals(sideString)) {
                return side;
            }
        }
        return null;
    }
}
