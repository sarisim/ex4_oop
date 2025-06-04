package pepse.world.clouds;

import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.ImageRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.world.JumpObserver;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/*
 * Rain class generates a rain effect by creating raindrop GameObjects that fall from a Cloud object.
 * It implements the JumpObserver interface to trigger the rain effect when a jump occurs.
 */
public class Rain implements JumpObserver {

    private final Cloud cloud;
    private final BiConsumer<GameObject, Integer> removeObjectFunc;
    private final BiConsumer<GameObject, Integer> addObjectFunc;
    private final ImageReader imageReader;
    private final Camera camera;

    /**
     * Constructor for Rain class.
     *
     * @param gameObjectAdder   Function to add a GameObject to the game.
     * @param gameObjectRemover Function to remove a GameObject from the game.
     * @param cloud             The Cloud object from which the rain originates.
     * @param imageReader       ImageReader for loading images.
     * @param camera            Camera for rendering the rain in the correct position.
     */
    public Rain(BiConsumer<GameObject, Integer> gameObjectAdder,
                BiConsumer<GameObject, Integer> gameObjectRemover,
                Cloud cloud,
                ImageReader imageReader,
                Camera camera) {
        this.addObjectFunc = gameObjectAdder;
        this.removeObjectFunc = gameObjectRemover;
        this.cloud = cloud;
        this.imageReader = imageReader;
        this.camera = camera;
    }

    /**
     * Creates a rain effect by generating raindrop GameObjects and adding them to the game.
     * The raindrops fall from the cloud's position and fade out over time.
     *
     * @param jump Indicates whether to create raindrops (true) or not (false).
     */
    @Override
    public void update(boolean jump) {
        if (!jump) return;

        List<GameObject> raindrops = new ArrayList<>();
        List<CloudBlock> blocks = cloud.getBlocks();
        if (blocks.isEmpty()) return;

        Vector2 cameraPos = camera.transform().getTopLeftCorner();
        Vector2 cloudCamTopLeft = blocks.get(0).getTopLeftCorner();
        Vector2 cloudWorldTopLeft = cloudCamTopLeft.add(cameraPos);

        float y = cloudWorldTopLeft.y() + cloud.getCloudHeight();
        float cloudWidth = cloud.getCloudWidth();

        Random rand = new Random();
        for (int i = 0; i < rand.nextInt(2, 6); i++) {
            float randXOffset = rand.nextFloat() * cloudWidth;
            float randX = cloudWorldTopLeft.x() + randXOffset;
            Renderable r = imageReader.readImage("assets/raindrop.png", true);

            GameObject raindrop = new GameObject(
                    new Vector2(randX, y),
                    new Vector2(20, 20),
                    r
            );
            raindrop.setCoordinateSpace(CoordinateSpace.WORLD_COORDINATES);
            raindrop.setVelocity(new Vector2(0, 100 + rand.nextFloat() * 100));
            raindrop.transform().setAccelerationY(600);
            addObjectFunc.accept(raindrop, Layer.BACKGROUND);
            raindrops.add(raindrop);
        }

        for (GameObject raindrop : raindrops) {
            new Transition<>(
                    raindrop,
                    (Float opacity) -> {
                        raindrop.renderer().setOpaqueness(opacity);
                        if (opacity <= 0f) {
                            removeObjectFunc.accept(raindrop, Layer.BACKGROUND);
                        }
                    },
                    1f,
                    0f,
                    Transition.LINEAR_INTERPOLATOR_FLOAT,
                    3f,
                    Transition.TransitionType.TRANSITION_ONCE,
                    null
            );
        }
    }

}
