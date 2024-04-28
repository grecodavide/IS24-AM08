package it.polimi.ingsw.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.ingsw.gamemodel.InitialCard;
import it.polimi.ingsw.gamemodel.Match;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;

public class Server {
    private List<Match> matches;
    public static final Map<Integer, Objective> objectives = new HashMap<>();
    public static final Map<Integer, PlayableCard> playableCards = new HashMap<>();
    public static final Map<Integer, InitialCard> initialCards = new HashMap<>();

    public Server() {
        matches = new ArrayList<>();
    }
    
    public List<Match> getJoinableMatches() {
        List<Match> joinableMatches = new ArrayList<>();
        for (Match match : matches) {
            if (!match.isFull()) {
                joinableMatches.add(match);
            }
        }
        return joinableMatches;
    }
    public static void main(String[] args) {
        
    }    
}
