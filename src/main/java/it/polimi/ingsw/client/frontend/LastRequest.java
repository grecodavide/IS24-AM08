package it.polimi.ingsw.client.frontend;

import it.polimi.ingsw.utils.RequestStatus;

/**
 * Last request status. Used for synchronized methods
 */
public class LastRequest {
    private RequestStatus status;

    /**
     * Sets the status.
     * 
     * @param status The new status
     */
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
    
    /**
     * @return The last request's status.
     */
    public RequestStatus getStatus() {
        return this.status;
    }
}
