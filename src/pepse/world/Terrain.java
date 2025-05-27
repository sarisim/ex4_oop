package pepse.world;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Terrain {
    private static final double FACTOR = Block.SIZE * 7;
    private final int groundHeightAtX0;
    private final int seed;
    private final float HEIGHT_RATIO = 0.66f;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 20;
    private final Vector2 winDims;
    private final NoiseGenerator noiseGenerator;

    public Terrain(Vector2 windowDimensions, int seed) {
        winDims = windowDimensions;
        groundHeightAtX0 = (int) (windowDimensions.y() * HEIGHT_RATIO);
        this.seed = seed;
        noiseGenerator = new NoiseGenerator(seed, groundHeightAtX0);

    }

    public float groundHeightAt(float x) {
        float noise = (float) noiseGenerator.noise(x, FACTOR);
        return groundHeightAtX0 + noise;
    }

    public List<Block> createInRange(int minX, int maxX) {
        List<Block> blocks = new ArrayList<>();
//        int range = maxX - minX;
//        int ratio_num = range / Block.SIZE + 1;
//        ratio_num *= Block.SIZE;
//        int startX = maxX - ratio_num;
        int startX = minX - (minX % Block.SIZE);
        int endX = maxX + Block.SIZE; // to ensure coverage

        for (int x = startX; x <= endX; x+= Block.SIZE) {
            double height = Math.floor(groundHeightAt(x) / Block.SIZE) * Block.SIZE;
//            int i = 0;
            for (int y = (int)height, i = 0; y < winDims.y() && i++ <TERRAIN_DEPTH; y+= Block.SIZE) {
                Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                Block block = new Block(new Vector2(x, y), renderable);
                block.setTag("ground");
                blocks.add(block);
            }
//            for (int y =(int)winDims.y(); y >=height; y -= Block.SIZE) {
//                Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
//                Block block = new Block(new Vector2(x, y), renderable);
//                block.setTag("ground");
//                blocks.add(block);
//            }

        }
        return blocks;
//        List<Block> blocks = new ArrayList<>();
//        Block block = new Block(Vector2.ZERO, new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
//        blocks.add(block);
//
//        return blocks;
    }

}
