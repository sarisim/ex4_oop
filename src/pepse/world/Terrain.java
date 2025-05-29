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
import java.util.function.BiConsumer;
//
//public class Terrain {
//    private static final double FACTOR = Block.SIZE * 7;
//    private final int groundHeightAtX0;
//    private final int seed;
//    private final float HEIGHT_RATIO = 0.66f;
//    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
//    private static final int TERRAIN_DEPTH = 20;
//    private final Vector2 winDims;
//    private final NoiseGenerator noiseGenerator;
//    private final List<GameObject> terrainBlocks = new ArrayList<>();
//    private int minX;
//    private int maxX;
//    private final java.util.Set<String> createdPositions = new java.util.HashSet<>();
//
//
//
//    public Terrain(Vector2 windowDimensions, int seed) {
//        winDims = windowDimensions;
//        groundHeightAtX0 = (int) (windowDimensions.y() * HEIGHT_RATIO);
//        this.seed = seed;
//        noiseGenerator = new NoiseGenerator(seed, groundHeightAtX0);
//
//    }
//
//    public float groundHeightAt(float x) {
//        float noise = (float) noiseGenerator.noise(x, FACTOR);
//        return groundHeightAtX0 + noise;
//    }
//
//    public List<Block> createInRange(int minX, int maxX,
//                                      BiConsumer<GameObject, Integer> gameObjectAdder) {
//        List<Block> blocks = new ArrayList<>();
//        int startX = minX - (minX % Block.SIZE);
//        int endX = maxX + Block.SIZE;
//
//        for (int x = startX; x <= endX; x+= Block.SIZE) {
//            double height = Math.floor(groundHeightAt(x) / Block.SIZE) * Block.SIZE;
//            for (int y = (int)height, i = 0; i++ < TERRAIN_DEPTH; y+= Block.SIZE) {
//                String key = x + "," + Math.round(y); // inside createInRange
//                if (createdPositions.contains(key))
//                    continue;
//
//                createdPositions.add(key);
//                Renderable renderable =
//                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
//                Block block = new Block(new Vector2(x, y), renderable);
//                block.setTag("ground");
//                blocks.add(block);
//                terrainBlocks.add(block);
//                gameObjectAdder.accept(block, Layer.STATIC_OBJECTS);
//            }
//        }
//        return blocks;
//    }
//
//    public void updateTerrainRange(int avatarX, int margin,
//                                   BiConsumer<GameObject, Integer> gameObjectAdder,
//                                   BiConsumer<GameObject, Integer> gameObjectRemover) {
//        int targetMin = avatarX - margin * 2;
//        if (targetMin < minX) {
//            createInRange(targetMin, minX, gameObjectAdder);
//            minX = targetMin;
//        }
//
//        int targetMax = avatarX + margin * 2;
//        if (targetMax > maxX) {
//            createInRange(maxX, targetMax, gameObjectAdder);
//            maxX = targetMax;
//        }
//    }
//}

public class Terrain {
    private static final double FACTOR = Block.SIZE * 7;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 20;
    private static final int CHUNK_SIZE = 512; // pixels

    private final int seed;
    private final int groundHeightAtX0;
    private final NoiseGenerator noiseGenerator;
    private final Vector2 winDims;

    private final Map<Integer, List<Block>> chunks = new HashMap<>();
    private final Set<Integer> loadedChunks = new HashSet<>();

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

    private List<Block> generateChunk(int chunkX) {
        List<Block> blocks = new ArrayList<>();
        int startX = chunkX * CHUNK_SIZE;
        int endX = startX + CHUNK_SIZE;

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

        return blocks;
    }

    public void updateTerrainRange(int avatarX, int margin,
                                   BiConsumer<GameObject, Integer> gameObjectAdder,
                                   BiConsumer<GameObject, Integer> gameObjectRemover) {
        int left = avatarX - margin * 2;
        int right = avatarX + margin * 2;
        int minChunk = left / CHUNK_SIZE;
        int maxChunk = right / CHUNK_SIZE;

        for (int cx = minChunk; cx <= maxChunk; cx++) {
            if (loadedChunks.contains(cx)) continue;
            List<Block> chunkBlocks = chunks.computeIfAbsent(cx, this::generateChunk);
            for (Block block : chunkBlocks) {
                gameObjectAdder.accept(block, Layer.STATIC_OBJECTS);
            }
            loadedChunks.add(cx);
        }

        Iterator<Integer> it = loadedChunks.iterator();
        while (it.hasNext()) {
            int cx = it.next();
            if (cx < minChunk - 1 || cx > maxChunk + 1) {
                List<Block> chunkBlocks = chunks.get(cx);
                for (Block block : chunkBlocks) {
                    gameObjectRemover.accept(block, Layer.STATIC_OBJECTS);
                }
                it.remove();
            }
        }
    }
}
