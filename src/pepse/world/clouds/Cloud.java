package pepse.world.clouds;
import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import danogl.components.Transition;
import pepse.util.ColorSupplier;
import pepse.world.JumpObserver;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Cloud  {
    private static final Color BASE_CLOUD_COLOR = new Color(255, 255, 255);
    private static final int CLOUD_ROWS = 6;
    private static final int CLOUD_COLS = 6;
    private static final Float MIDNIGHT_OPACITY = 0.5f;
    private static final Float INITIAL_CLOUD_VAL = 1f;
    private final List<CloudBlock> blocks;
    private final Vector2 cloudOrigin;
    private final float blockSize;
    private final Vector2 windowDims;

    public Cloud(Vector2 windowDims, float blockSize) {
        this.blockSize = blockSize;
        this.windowDims = windowDims;
        this.blocks = new ArrayList<>();
        List<List<Integer>> structure = generateRandomCloudList(CLOUD_ROWS, CLOUD_COLS);
        this.cloudOrigin = new Vector2(-blockSize * CLOUD_COLS, windowDims.y()/5);
        Vector2 pos = new Vector2(cloudOrigin.x(), cloudOrigin.y());
        for (int i = 0; i < structure.size(); i++) {
            for (int j = 0; j < structure.get(i).size(); j++) {
                if (structure.get(i).get(j) == 1) {
                    Vector2 pos2 = pos.add(new Vector2(j * blockSize, i * blockSize));
                    Renderable renderable = new RectangleRenderable(
                            ColorSupplier.approximateMonoColor(BASE_CLOUD_COLOR));
                    CloudBlock block = new CloudBlock(pos2, new Vector2(blockSize, blockSize), renderable);
                    blocks.add(block);
                }
            }
        }

        for (CloudBlock block : blocks) {
            block.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        }

        transitionSpace();
        transitionColor();
    }

    private void transitionColor() {

        Transition<Float> transition = new Transition<>(
            blocks.get(0),
                (Float opacity) -> {
            for (CloudBlock block : blocks) {
                block.renderer().setOpaqueness( opacity);
            }
        },
            INITIAL_CLOUD_VAL,
            MIDNIGHT_OPACITY, 
                    Transition.CUBIC_INTERPOLATOR_FLOAT,
                    30,
            Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    null);
            
    }

    private void transitionSpace() {
        new Transition<>(
                blocks.get(0),
                (Float offsetX) -> {
                    for (CloudBlock block : blocks) {
                        Vector2 original = block.getInitialPosition();
                        block.setTopLeftCorner(new Vector2(original.x() + offsetX, original.y()));
                    }
                },
                cloudOrigin.x(),
                windowDims.x() - cloudOrigin.x(),
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                15,
                Transition.TransitionType.TRANSITION_LOOP,
                null
        );
    }

    private List<List<Integer>> generateRandomCloudList(int rows, int cols) {
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                row.add(Math.random() < 0.7 ? 1 : 0); // 70% chance for cloud block
            }
            result.add(row);
        }
        return result;
    }

    public List<CloudBlock> getBlocks() {
        return blocks;
    }


    public Vector2 getPosition() {
        return new Vector2( blocks.get(0).getTopLeftCorner().x(), cloudOrigin.y() + (blockSize * CLOUD_ROWS));
    }

    public float getYPosition() {
        return cloudOrigin.y();
    }

    public float getCloudBound() {
        return (CLOUD_COLS * blockSize);
    }

    public float getCloudHeight()  {
        return (CLOUD_ROWS * blockSize);
    }

    public float getCloudWidth() {
        return (CLOUD_COLS * blockSize);
    }
}

