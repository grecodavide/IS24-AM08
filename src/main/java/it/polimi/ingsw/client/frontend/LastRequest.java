package it.polimi.ingsw.client.frontend;

import it.polimi.ingsw.utils.RequestStatus;

/**
 * LastRequest
 */

public class LastRequest {
    private RequestStatus status;

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public RequestStatus getStatus() {
        return this.status;
    }
}
