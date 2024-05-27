package it.polimi.ingsw.client.frontend.tui;

/**
 * PlayerControls
 */

public class PlayerControls {
    private boolean enabled;

    public PlayerControls() {
        this.enabled = false;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }
}
