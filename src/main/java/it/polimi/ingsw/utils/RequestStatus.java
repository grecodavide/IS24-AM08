package it.polimi.ingsw.utils;

import it.polimi.ingsw.controllers.PlayerController;
import it.polimi.ingsw.server.Server;

/**
 * Represents the status of a remote request sent to a remove {@link Server} or {@link PlayerController}.
 */
public enum RequestStatus {
    PENDING,
    SUCCESSFUL,
    FAILED;
}
