package it.polimi.ingsw.utils;

/**
 * LeaderboardEntry
 *
 * @param username The username of the player corresponding to this entry
 * @param points the points obtained by the player during the game
 * @param winner wheter the player is the winner or not (in case of draw, only the player with most objectives fullfilled wins)
 */
public record LeaderboardEntry(String username, Integer points, boolean winner) { }
