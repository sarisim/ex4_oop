package pepse.world;
//
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Terrain {
    private static final double FACTOR = Block.SIZE * 7;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 20;

    private final int seed;
    private final int groundHeightAtX0;
    private final NoiseGenerator noiseGenerator;
    private final Vector2 winDims;

    public Terrain(Vector2 windowDimensions, int seed) {
        this.seed = seed;
        this.winDims = windowDimensions;
        this.groundHeightAtX0 = (int) (windowDimensions.y() * 0.66f);
        this.noiseGenerator = new NoiseGenerator(seed, groundHeightAtX0);
    }

    public float groundHeightAt(float x) {
        float noise = (float) noiseGenerator.noise(x, FACTOR);
        return groundHeightAtX0 + noise;
    }

    public List<Block> createInRange(int startX, int endX) {
        List<Block> blocks = new ArrayList<>();

        for (int x = startX; x < endX; x += Block.SIZE) {
            float height = groundHeightAt(x);
            int groundTop = (int) (Math.floor(height / Block.SIZE) * Block.SIZE);
            for (int y = groundTop, i = 0; i < TERRAIN_DEPTH; i++, y += Block.SIZE) {
                Vector2 pos = new Vector2(x, y);
                Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                Block block = new Block(pos, renderable);
                block.setTag("ground");
                blocks.add(block);
            }
        }
        System.out.println("Created " + blocks.size() + " blocks");
        return blocks;
    }

    public int getBlockSize() {
        return Block.SIZE;
    }

//    public void updateTerrainRange(int avatarX, int margin,
//                                   BiConsumer<GameObject, Integer> gameObjectAdder,
//                                   BiConsumer<GameObject, Integer> gameObjectRemover) {
//        int left = avatarX - margin * 2;
//        int right = avatarX + margin * 2;
//        int minChunk = left / CHUNK_SIZE;
//        int maxChunk = right / CHUNK_SIZE;
//
//        for (int cx = minChunk; cx <= maxChunk; cx++) {
//            if (loadedChunks.contains(cx)) continue;
//            List<Block> chunkBlocks = chunks.computeIfAbsent(cx, this::generateChunk);
//            for (Block block : chunkBlocks) {
//                gameObjectAdder.accept(block, Layer.STATIC_OBJECTS);
//            }
//            loadedChunks.add(cx);
//        }
//
//        Iterator<Integer> it = loadedChunks.iterator();
//        while (it.hasNext()) {
//            int cx = it.next();
//            if (cx < minChunk - 1 || cx > maxChunk + 1) {
//                List<Block> chunkBlocks = chunks.get(cx);
//                for (Block block : chunkBlocks) {
//                    gameObjectRemover.accept(block, Layer.STATIC_OBJECTS);
//                }
//                it.remove();
//            }
//        }
//    }
}
