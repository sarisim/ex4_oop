package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;


public class SunHalo {

    public static GameObject create(GameObject sun) {
        GameObject halo = new GameObject(sun.getTopLeftCorner(), sun.getDimensions().mult(1.5f),
                new OvalRenderable(new Color(255, 255, 0, 20)));
        halo.setTag("sunHalo");
        halo.addComponent( deltaTime -> halo.setCenter(sun.getCenter()));
        halo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);

        return halo;
    }
}
