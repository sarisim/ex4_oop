package pepse.world;

/**
 * An interface for observing jump events in the game.
 * Implementing classes can define actions to take when a jump occurs.
 */
public interface JumpObserver {
    /**
     * Called when a jump event occurs.
     *
     * @param jump {@code true} if a jump was initiated, {@code false} otherwise.
     */
    void update(boolean jump);
}
