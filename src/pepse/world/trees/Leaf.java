package pepse.world.trees;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.Random;
/**
 * Represents a leaf in the game world.
 * The leaf has a specific color, size, and transitions for angle and size.
 * It randomly changes its state over time.
 */
public class Leaf extends GameObject {
    private static final Color LEAF_COLOR = new Color(50, 200, 30);
    private static final int RESIZE_TRANSITION_TIME = 10;
    private static final int ANGLE_TRANSITION_TIME = 1;
    private static final float ANGLE = 30f;
    private static final int FLOAT_GENERTOR_BOUND = 5;
    /** The length of the leaf in pixels.
     * This is used to define the size of the leaf.
     */
    public static int LEAF_LENGTH = 20;
    private final static Vector2 LEAF_SIZE = new Vector2(LEAF_LENGTH,LEAF_LENGTH);
    private final static Vector2 LEAF_SMALL_SIZE = new Vector2(15,15);
    private final Random rand = new Random();

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     */
    private Leaf(Vector2 topLeftCorner) {
        super(topLeftCorner, LEAF_SIZE, new RectangleRenderable(ColorSupplier.approximateColor( LEAF_COLOR)));
    }
    /**
     * Creates a new Leaf object at the specified position.
     * The leaf will have transitions for angle and size, and will randomly change its state.
     *
     * @param topLeftCorner Position of the leaf in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @return A new Leaf object with transitions set up.
     */
    public static Leaf createLeaf (Vector2 topLeftCorner){
        Leaf leaf = new Leaf(topLeftCorner);
        createTransitions(leaf);

        new ScheduledTask(
            leaf,
            leaf.randomWaitTime(),
            true,
            ()->createTransitions(leaf));
    return leaf;
    }

    /**
     * Creates transitions for the leaf's angle and size.
     * The angle will oscillate between -ANGLE and ANGLE, and the size will oscillate
     * between LEAF_SIZE and LEAF_SMALL_SIZE.
     *
     * @param leaf The Leaf object to which the transitions will be applied.
     */
    private static void createTransitions(Leaf leaf) {
        new Transition<>(leaf,
                leaf.renderer()::setRenderableAngle,
                -ANGLE,
                ANGLE,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                ANGLE_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
        new Transition<>(leaf,
                leaf::setDimensions,
                LEAF_SIZE,
                LEAF_SMALL_SIZE,
                Transition.CUBIC_INTERPOLATOR_VECTOR,
                RESIZE_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
    }
    /**
     * Generates a random wait time for the leaf's transitions.
     * The wait time is a random float value between 0 and FLOAT_GENERTOR_BOUND.
     *
     * @return A random float value representing the wait time.
     */
    private float randomWaitTime(){
        return rand.nextFloat(FLOAT_GENERTOR_BOUND);
    }

}
