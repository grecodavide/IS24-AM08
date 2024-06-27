package it.polimi.ingsw.client.frontend.tui;

/**
 * Class used to synchronyze methods that should be ran only during other player's turns.
 */
public class PlayerControls {
    private boolean enabled;

    
    /**
     * Class constructor. Starts disabled.
     */
    public PlayerControls() {
        this.enabled = false;
    }

    
    /**
     * @return Whether the player's custom controls are enabled or not.
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    
    /**
     * Enables player's custom controls, and notifies all threads synchronized on this object.
     */
    public synchronized void enable() {
        this.enabled = true;
        this.notifyAll();
    }

    
    /**
     * Disables the player's controls.
     */
    public void disable() {
        this.enabled = false;
    }
}
