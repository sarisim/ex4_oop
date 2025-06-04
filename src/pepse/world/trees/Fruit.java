package pepse.world.trees;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents a fruit in the game world.
 * The fruit can be eaten by the avatar, which will trigger a respawn after a certain time.
 */
public class Fruit extends GameObject {
    private static final int FRUIT_RESPAWN_TIME = 30;
    private static final Color FRUIT_COLOR = new Color(200, 50, 30);
    private static final  Vector2 FRUIT_SIZE = new Vector2(15, 15);


    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     */
    public Fruit(Vector2 topLeftCorner) {
        super(topLeftCorner, FRUIT_SIZE, new OvalRenderable(FRUIT_COLOR));
    }

    /**
     * Removes the fruit from the game world and schedules its respawn after a certain time.
     * The fruit will become invisible (opaqueness set to 0) when eaten.
     */
    public void removeAndAddFruit() {
        renderer().setOpaqueness(0);
        this.setTag("eaten fruit");
        new ScheduledTask(
                this,
                FRUIT_RESPAWN_TIME,
                false,
                () -> {
                    this.renderer().setOpaqueness(1);
                    this.setTag("fruit");
                }
        );
    }
}
