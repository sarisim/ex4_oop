package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;
/** * Represents the night in the game world.
 * The night is a GameObject that covers the entire screen with a semi-transparent black rectangle.
 * It transitions from fully transparent to semi-transparent to simulate nightfall.
 */
public class Night {

    private static final float MIDNIGHT_OPACITY = 0.5f;
    private static final float INITIAL_NIGHT_VALUE = 0f;

    /**
     * Creates a GameObject representing the night.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength      The length of the day-night cycle in seconds.
     * @return A GameObject that represents the night.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions,
                new RectangleRenderable(Color.BLACK));
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag("night");

        new Transition<>(night,
                night.renderer()::setOpaqueness,
                INITIAL_NIGHT_VALUE,
                MIDNIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                30,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);

        return night;
    }
}
