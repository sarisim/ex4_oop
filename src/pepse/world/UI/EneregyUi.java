package pepse.world.UI;

import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.world.Avatar;

import java.util.function.Supplier;

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
        super(topLeftCorner, dimensions, new TextRenderable(String.valueOf(func.get())));
        this.energyFunc = func;
        this.energy = func.get();

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        this.energy = energyFunc.get();
        renderer().setRenderable(new TextRenderable(String.valueOf(energyFunc.get())));
    }
}
