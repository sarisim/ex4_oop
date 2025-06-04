package pepse.world.UI;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.util.function.Supplier;

/**
 * Represents a UI element that displays the player's energy level.
 * The energy level is updated based on a supplier function that provides the current energy value.
 */
public class EneregyUi extends GameObject {

    private final Supplier<Float> energyFunc;
    private float energy;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     */
    public EneregyUi(Vector2 topLeftCorner, Vector2 dimensions,Supplier<Float> func) {
        super(topLeftCorner, dimensions, new TextRenderable(String.valueOf((int)Math.floor(func.get()))));
        this.energyFunc = func;
        this.energy = func.get();
        setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

    /**
     * Sets the energy level to a new value.
     *
     * @param deltaTime The new energy level to set.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        this.energy = energyFunc.get();
        renderer().setRenderable(new TextRenderable(
                String.valueOf((int)Math.floor(this.energy)) + "%"));
    }
}