package it.polimi.ingsw.server;

import java.util.ArrayList;
import java.util.List;
import it.polimi.ingsw.gamemodel.*;

public class Server {
    private List<Match> matches;

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
