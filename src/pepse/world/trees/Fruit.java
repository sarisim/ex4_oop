package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Avatar;

import java.awt.*;

public class Fruit extends GameObject {
    public static final int FRUIT_RESPWAN_TIME = 30;
    private static final Color FRUIT_COLOR = new Color(200, 50, FRUIT_RESPWAN_TIME);
    private final static Vector2 FRUIT_SIZE = new Vector2(15,15);


    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     */
    public Fruit(Vector2 topLeftCorner) {
        super(topLeftCorner, FRUIT_SIZE, new OvalRenderable(FRUIT_COLOR));
    }

    public void removeAndAddFruit(){
        renderer().setOpaqueness(0);
        this.setTag("eaten fruit");
        new ScheduledTask(
                this,
                FRUIT_RESPWAN_TIME,
                false,
                ()->{this.renderer().setOpaqueness(1);
                this.setTag("fruit");}
        );
    }
}
