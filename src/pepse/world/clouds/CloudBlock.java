package pepse.world.clouds;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;

public class CloudBlock extends GameObject {
    private Vector2 initialPosition;

    public CloudBlock(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
        this.initialPosition = topLeftCorner;
    }

    public Vector2 getInitialPosition() {
        return initialPosition;
    }

    public void setInitialPosition(Vector2 pos) {
        this.initialPosition = pos;
        setTopLeftCorner(pos);
    }
}


