package pepse.world;

/**
 * An interface for observing jump events in the game.
 * Implementing classes can define actions to take when a jump occurs.
 */
public interface JumpObserver {
    void update(boolean jump);
}
