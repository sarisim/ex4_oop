package pepse.world.clouds;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;

/**
 * Represents a cloud block in the game world.
 * A CloudBlock is a GameObject that can be rendered and has an initial position.
 */
public class CloudBlock extends GameObject {
    private Vector2 initialPosition;

    /**
     * Constructs a new CloudBlock instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    The dimensions of the block.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public CloudBlock(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
        this.initialPosition = topLeftCorner;
    }

    /**
    * a getter for the initial position of the CloudBlock.
    *
    */
    public Vector2 getInitialPosition() {
        return initialPosition;
    }

    /**
     * Sets the initial position of the CloudBlock.
     *
     * @param pos The new initial position to set.
     */
    public void setInitialPosition(Vector2 pos) {
        this.initialPosition = pos;
        setTopLeftCorner(pos);
    }
}


