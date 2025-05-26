package pepse.world;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Terrain {
    private final int groundHeightAtX0;
    private final int seed;
    private final float HEIGHT_RATIO = 0.66f;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private final Vector2 winDims;

    public Terrain(Vector2 windowDimensions, int seed) {
        winDims = windowDimensions;
        groundHeightAtX0 = (int) (windowDimensions.y() * HEIGHT_RATIO);
        this.seed = seed;

    }

    public float groundHeightAt(float x) {
        return groundHeightAtX0;
    }

    public List<Block> createInRange(int minX, int maxX) {
        List<Block> blocks = new ArrayList<>();
        for (int x = minX; x <= maxX; x+= Block.SIZE) {
            float height = groundHeightAt(x);
            for (int y =(int)winDims.y(); y >=height; y -= Block.SIZE) {
                Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                Block block = new Block(new Vector2(x, y), renderable);
                block.setTag("ground");
                blocks.add(block);
            }

        }
        return blocks;
//        List<Block> blocks = new ArrayList<>();
//        Block block = new Block(Vector2.ZERO, new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
//        blocks.add(block);
//
//        return blocks;
    }

}
