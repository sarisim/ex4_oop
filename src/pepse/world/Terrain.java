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
/**
 * Represents the terrain in the game world.
 * The terrain is generated using Perlin noise and consists of blocks that form the ground.
 * It provides methods to calculate ground height at a specific x-coordinate and to create blocks
 * in a specified range.
 */
public class Terrain {
    private static final double FACTOR = Block.SIZE * 7;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 25;

    private final int groundHeightAtX0;
    private final NoiseGenerator noiseGenerator;
    private final Vector2 winDims;

    /**
     * Constructs a Terrain object with specified window dimensions and seed for noise generation.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param seed             The seed for the noise generator to create consistent terrain.
     */
    public Terrain(Vector2 windowDimensions, int seed) {
        this.winDims = windowDimensions;
        this.groundHeightAtX0 = (int) (windowDimensions.y() * 0.66f);
        this.noiseGenerator = new NoiseGenerator(seed, groundHeightAtX0);
    }

    /**
     * Calculates the ground height at a specific x-coordinate using Perlin noise.
     *
     * @param x The x-coordinate for which to calculate the ground height.
     * @return The ground height at the specified x-coordinate.
     */
    public float groundHeightAt(float x) {
        float noise = (float) noiseGenerator.noise(x, FACTOR);
        return groundHeightAtX0 + noise;
    }

    /**
     * Creates a list of blocks representing the terrain in a specified range.
     * Each block is positioned at the ground height calculated for its x-coordinate.
     *
     * @param startX The starting x-coordinate for the range.
     * @param endX   The ending x-coordinate for the range.
     * @return A list of Block objects representing the terrain in the specified range.
     */
    public List<Block> createInRange(int startX, int endX) {
        List<Block> blocks = new ArrayList<>();

        for (int x = startX; x < endX; x += Block.SIZE) {
            float height = groundHeightAt(x);
            int groundTop = (int) (Math.floor(height / Block.SIZE) * Block.SIZE);
            for (int y = groundTop, i = 0; i < TERRAIN_DEPTH; i++, y += Block.SIZE) {
                Vector2 pos = new Vector2(x, y);
                Renderable renderable =
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                Block block = new Block(pos, renderable);
                block.setTag("ground");
                blocks.add(block);
            }
        }
        return blocks;
    }

    /**
     *  getter for the block size used in the terrain.
     *
     */
    public int getBlockSize() {
        return Block.SIZE;
    }

}
