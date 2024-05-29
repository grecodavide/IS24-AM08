package it.polimi.ingsw.network.messages.responses;

import java.util.Map;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.gamemodel.Symbol;

/**
 * This response is sent to each user in the match when a user chosees the initial side of a card.
 */
public final class SomeoneSetInitialSideMessage extends ResponseMessage {
    private final Side side;
    private final Map<Symbol, Integer> availableResources;

    
    /**
     * @return Available resources of player after setting the initial card
     */
    public Map<Symbol, Integer> getAvailableResources() {
        return availableResources;
    }

    /**
     * @return Side of the initial card.
     */
    public Side getSide() {
        return side;
    }

    public SomeoneSetInitialSideMessage(String username, Side side, Map<Symbol, Integer> availableResources) {
        super(username);
        this.side = side;
        this.availableResources = availableResources;
    }
}
