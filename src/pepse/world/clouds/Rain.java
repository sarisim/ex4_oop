package pepse.world.clouds;

import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.gui.rendering.AnimationRenderable;
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

public class Rain implements JumpObserver {

    private final Cloud cloud;
    private final PepseGameManager game;
    private final ImageReader imageReader;


    public Rain(PepseGameManager game, Cloud cloud, ImageReader imageReader) {
        this.game = game;
        this.cloud = cloud;
        this.imageReader = imageReader;
    }

    @Override
    public void update(boolean jump) {
        if (jump) {
            List<GameObject> raindrops = new ArrayList<>();
            Vector2 cloudPos = cloud.getPosition();
            float cloudBound = cloud.getCloudBound();

            Random rand = new Random();
            for (int i = 0; i<rand.nextInt(6); i++) {
                float randX = cloudPos.x() + rand.nextFloat() * cloudBound;
                Renderable r = imageReader.readImage("assets/raindrop.png",true);
                GameObject raindrop = new GameObject(new Vector2(randX, cloudPos.y()), new Vector2(20, 20), r);
                Vector2 velocity = new Vector2(0, 100 + rand.nextFloat() * 100); // 100–200 px/sec
                raindrop.setVelocity(velocity);
                game.addObject(raindrop, Layer.BACKGROUND);
                raindrops.add(raindrop);
            }

            for (GameObject raindrop : raindrops) {
                new Transition<>(
                        raindrop,
                        (Float opacity) -> {
                            raindrop.renderer().setOpaqueness(opacity);
                            if (opacity <= 0f) {
                                game.removeObject(raindrop);
                            }
                        },
                        1f,
                        0f,
                        Transition.LINEAR_INTERPOLATOR_FLOAT,
                        3f, // fade-out duration in seconds
                        Transition.TransitionType.TRANSITION_ONCE,
                        null
                );

            }
        }


    }
}
