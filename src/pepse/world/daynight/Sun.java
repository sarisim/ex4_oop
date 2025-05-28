package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import java.awt.*;
import danogl.components.Transition;


public class Sun {

    private static final Vector2 SUN_DIMENSIONS = new Vector2(100, 100);
    private static final float HEIGHT_RATIO = 0.66f;
    public static final Float STARTING_POINT_SUN_ANGLE = 0f;
    public static final Float ENDING_POINT_SUN_ANGLE = 360f;
    public static Vector2 cycleCenter;

    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        int groundHeightAtX0 = (int) (windowDimensions.y() * HEIGHT_RATIO);
        float skyHeight = (windowDimensions.y() * (1 - HEIGHT_RATIO));
        GameObject sun = new GameObject(new Vector2(windowDimensions.x()/2, skyHeight/2), SUN_DIMENSIONS,
                new OvalRenderable(Color.YELLOW));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag("sun");
        cycleCenter = new Vector2(windowDimensions.x()/2, groundHeightAtX0);
        Vector2 initialSunCenter = sun.getCenter();

        Transition<Float> transition = new Transition<>(
                sun,
                (angle) -> sun.setCenter
                        (initialSunCenter.subtract(cycleCenter)
                                .rotated(angle)
                                .add(cycleCenter)), //fix here, had to change the types from what it said in instructions
                STARTING_POINT_SUN_ANGLE,
                ENDING_POINT_SUN_ANGLE ,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                60,
                Transition.TransitionType.TRANSITION_LOOP,
                null);

//        sun.addComponent(positionTransition);
        return sun;
    }


}
