package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

public class Leaf extends GameObject {
    private static final Color LEAF_COLOR = new Color(50, 200, 30);
    public static int LEAF_LENGTH = 20;
    public static Vector2 LEAF_SIZE = new Vector2(LEAF_LENGTH,LEAF_LENGTH);
    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     */
    public Leaf(Vector2 topLeftCorner) {
        super(topLeftCorner, LEAF_SIZE, new RectangleRenderable(LEAF_COLOR));
    }
}
