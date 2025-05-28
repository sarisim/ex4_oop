package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

import static pepse.world.daynight.Sun.ENDING_POINT_SUN_ANGLE;
import static pepse.world.daynight.Sun.STARTING_POINT_SUN_ANGLE;

public class SunHalo {

    public static GameObject create(GameObject sun) {
        GameObject halo = new GameObject(sun.getCenter(), sun.getDimensions().mult(1.5f),
                new OvalRenderable(new Color(255, 255, 0, 20)));        halo.setTag("sunHalo");
        halo.setCoordinateSpace(sun.getCoordinateSpace());

        // Set up the transition for the sun's position and opacity
        halo.addComponent(deltaTime -> halo.setCenter(sun.getCenter()));
        return halo;
    }
}
