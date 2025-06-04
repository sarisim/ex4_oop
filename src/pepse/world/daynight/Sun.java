package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import danogl.components.Transition;
import java.awt.*;

/**
 * Represents the sun in the game world.
 * The sun is a GameObject that moves in a circular path across the sky,
 * simulating a day-night cycle.
 */
public class Sun {

    private static final Vector2 SUN_DIMENSIONS = new Vector2(75, 75);
    private static final float HEIGHT_RATIO = 0.66f;
    /**
     * The starting and ending angles for the sun's movement in degrees.
     * The sun will move from 0 degrees (east) to 360 degrees (back to east).
     */
    protected static final Float STARTING_POINT_SUN_ANGLE = 0f;
    /**
     * The ending angle for the sun's movement in degrees.
     * This is set to 360 degrees to complete a full cycle.
     */
    protected static final Float ENDING_POINT_SUN_ANGLE = 360f;
    private static Vector2 cycleCenter;

    /**
     * Creates a GameObject representing the sun.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength      The length of the day-night cycle in seconds.
     * @return A GameObject that represents the sun.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        int groundHeightAtX0 = (int) (windowDimensions.y() * HEIGHT_RATIO);
        float skyHeight = (windowDimensions.y() * (1 - HEIGHT_RATIO));
        GameObject sun = new GameObject(
                new Vector2(windowDimensions.x() / 2, skyHeight / 2), SUN_DIMENSIONS,
                new OvalRenderable(Color.YELLOW));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag("sun");
        cycleCenter = new Vector2(windowDimensions.x() / 2, groundHeightAtX0);
        Vector2 initialSunCenter = sun.getCenter();
        Transition<Float> transition = new Transition<Float>(
                sun,
                (Float angle) -> sun.setCenter
                        (initialSunCenter.subtract(cycleCenter)
                                .rotated(angle)
                                .add(cycleCenter)),
                STARTING_POINT_SUN_ANGLE,
                ENDING_POINT_SUN_ANGLE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP, null

        );

        return sun;
    }

}
