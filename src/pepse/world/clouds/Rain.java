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

public class Rain implements JumpObserver {

    private final Cloud cloud;
    private final BiConsumer<GameObject, Integer> removeObjectFunc;
    private final BiConsumer<GameObject, Integer> addObjectFunc;
    private final ImageReader imageReader;
    private final Camera camera;


    public Rain(BiConsumer<GameObject, Integer> gameObjectAdder, BiConsumer<GameObject, Integer> gameObjectRemover,
             Cloud cloud, ImageReader imageReader, Camera camera) {
        this.addObjectFunc = gameObjectAdder;
        this.removeObjectFunc = gameObjectRemover;
        this.cloud = cloud;
        this.imageReader = imageReader;
        this.camera = camera;
    }

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


//    @Override
//    public void update(boolean jump) {
//        if (jump) {
//            List<GameObject> raindrops = new ArrayList<>();
//            List<CloudBlock> blocks = cloud.getBlocks();
////            if (blocks.isEmpty()) return;
////            float minX = Float.MAX_VALUE;
////            float maxX = Float.MIN_VALUE;
//            Vector2 topLeftCorner = blocks.get(0).getInitialPosition();
//            float y = topLeftCorner.y() + cloud.getCloudHeight();
//            float cloudWidth = cloud.getCloudWidth();
////            Vector2 cloudPos = cloud.getPosition();
////            float cloudBound = cloud.getCloudBound();
//
//            Random rand = new Random();
//            for (int i = 0; i<rand.nextInt(2,6); i++) {
//                float randX = topLeftCorner.x() + rand.nextFloat() * cloudWidth;
//                Renderable r = imageReader.readImage("assets/raindrop.png",true);
//                GameObject raindrop = new GameObject(new Vector2(randX,y), new Vector2(20, 20), r);
//                raindrop.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
//                Vector2 worldPos = raindrop.getCenter();
//                raindrop.setCoordinateSpace(CoordinateSpace.WORLD_COORDINATES);
//                raindrop.setCenter(worldPos);
//                Vector2 velocity = new Vector2(0, 100 + rand.nextFloat() * 100); // 100–200 px/sec
//                raindrop.setVelocity(velocity);
//                this.addObjectFunc.accept(raindrop, Layer.BACKGROUND);
//                raindrops.add(raindrop);
//                raindrop.transform().setAccelerationY(600);
//            }
//
//            for (GameObject raindrop : raindrops) {
//                new Transition<>(
//                        raindrop,
//                        (Float opacity) -> {
//                            raindrop.renderer().setOpaqueness(opacity);
//                            if (opacity <= 0f) {
//                                removeObjectFunc.accept(raindrop, Layer.BACKGROUND);
//                            }
//                        },
//                        1f,
//                        0f,
//                        Transition.LINEAR_INTERPOLATOR_FLOAT,
//                        3f, // fade-out duration in seconds
//                        Transition.TransitionType.TRANSITION_ONCE,
//                        null
//                );
//
//            }
//        }
//
//
//    }
}
