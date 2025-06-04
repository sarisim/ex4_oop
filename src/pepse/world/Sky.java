package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents the sky in the game world.
 * The sky is a GameObject that covers the entire screen with a solid color.
 */
public class Sky {
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");

    /**
     * Creates a GameObject representing the sky.
     *
     * @param windowDimensions The dimensions of the game window.
     * @return A GameObject that represents the sky.
     */
    public static GameObject create(Vector2 windowDimensions) {
        GameObject sky = new GameObject(Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sky.setTag("sky");
        return sky;
    }

}
