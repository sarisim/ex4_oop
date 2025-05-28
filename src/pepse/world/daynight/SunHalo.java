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
                new OvalRenderable(Color.YELLOW));
        halo.setTag("sunHalo");
        halo.setCoordinateSpace(sun.getCoordinateSpace());
//        halo.setRenderable(sun.renderer().getRenderable());
        halo.renderer().setOpaqueness(0.5f); // Set a semi-transparent effect

        Vector2 initialSunCenter = sun.getCenter();
        Vector2 cycleCenter = Sun.cycleCenter;
        // Set up the transition for the sun's position and opacity
        Transition<Float> transition = new Transition<>(
                sun,
                (angle) -> sun.setCenter
                        (initialSunCenter.subtract(sun.getCenter())
                                .rotated((float)angle)
                                .add(cycleCenter)), //fix here, had to change the types from what it said in instructions
                STARTING_POINT_SUN_ANGLE,
                ENDING_POINT_SUN_ANGLE ,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                60,
                Transition.TransitionType.TRANSITION_LOOP,
                null);
        return halo;
    }
}
