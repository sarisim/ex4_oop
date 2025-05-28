package pepse.world.trees;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

public class Trunk extends GameObject {
    private static final Color TRUNK_COLOR = new Color(100, 50, 20);

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     *      */
    public Trunk(Vector2 topLeftCorner, Vector2 dimensions) {
        super(topLeftCorner, dimensions, new RectangleRenderable(TRUNK_COLOR));
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);

    }
}
