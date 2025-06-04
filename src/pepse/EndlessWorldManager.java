package pepse;

import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Terrain;
import pepse.world.trees.Flora;
import pepse.world.trees.Tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Manages the endless world generation by creating and removing terrain blocks and trees
 * based on the position of the object to follow (usually the player).
 * It ensures that the terrain and flora are generated dynamically as the player moves.
 */
public class EndlessWorldManager {
    private final Terrain terrain;
    private final Flora flora;
    private final BiConsumer<GameObject, Integer> gameObjectAdder;
    private final BiConsumer<GameObject, Integer> gameObjectRemover;
    private final GameObject objectToFollow;
    private final Vector2 windowDims;
    private final List<GameObject> currTerrainBlocks = new ArrayList<>();
    private final List<GameObject> currTrees = new ArrayList<>();
    private final HashSet<Integer> currChunks = new HashSet<>();
    private int blockSize;

    /**
     * Constructs an EndlessWorldManager instance.
     *
     * @param terrain          The terrain object used for generating blocks.
     * @param flora            The flora object used for generating trees.
     * @param gameObjectAdder  Function to add a GameObject to the game.
     * @param gameObjectRemover Function to remove a GameObject from the game.
     * @param objectToFollow   The GameObject that the camera will follow (usually the player).
     * @param windowDims       The dimensions of the game window.
     */
    public EndlessWorldManager(Terrain terrain, Flora flora, BiConsumer<GameObject, Integer> gameObjectAdder,
                               BiConsumer<GameObject, Integer> gameObjectRemover,
                               GameObject objectToFollow, Vector2 windowDims) {
        this.terrain = terrain;
        this.blockSize = terrain.getBlockSize();
        this.flora = flora;
        this.gameObjectAdder = gameObjectAdder;
        this.gameObjectRemover = gameObjectRemover;
        this.objectToFollow = objectToFollow;
        this.windowDims = windowDims;
    }

    /**
     * Updates the world by generating or removing terrain blocks and trees based on the position of the
     * object to follow.
     *
     * @param deltaTime The time elapsed since the last update, not used in this method but required
     *                 by the interface.
     */
    public void update(float deltaTime) {
        Vector2 center = objectToFollow.getCenter();

        int minChunk = (int) Math.floor((center.x() - windowDims.x() / 2) / blockSize);
        int maxChunk = (int) Math.ceil((center.x() + windowDims.x() / 2) / blockSize);
        currTerrainBlocks.removeIf(block -> {
            if (!checkIfInBounds(block, minChunk, maxChunk)) {
                gameObjectRemover.accept(block, Layer.STATIC_OBJECTS);
                return true;
            }
            return false;
        });
        currTrees.removeIf(tree -> {
            if (!checkIfInBounds(tree, minChunk, maxChunk)) {
                gameObjectRemover.accept(tree, Layer.STATIC_OBJECTS);
                return true;
            }
            return false;
        });
        for (int chunk = minChunk; chunk <= maxChunk; chunk++) {
            if (!currChunks.contains(chunk)) {
                int minX = chunk * blockSize;
                int maxX = minX + blockSize;
                List<Block> newTerrainBlocks = terrain.createInRange(minX, maxX);
                for (Block block : newTerrainBlocks) {
                    gameObjectAdder.accept(block, Layer.STATIC_OBJECTS);
                    currTerrainBlocks.add(block);
                }
                List<Tree> newTrees = flora.createInRange(minX, maxX);
                for (Tree tree : newTrees) {
                    gameObjectAdder.accept(tree.getTrunk(), Layer.DEFAULT);
                    currTrees.add(tree.getTrunk());
                    for (GameObject leaf : tree.getLeaves()) {
                        gameObjectAdder.accept(leaf, Layer.STATIC_OBJECTS);
                        currTrees.add(leaf);
                    }
                    for (GameObject fruit : tree.getFruits()) {
                        gameObjectAdder.accept(fruit, Layer.DEFAULT);
                        currTrees.add(fruit);
                    }
                }
            currChunks.add(chunk);
            }
        }
    currChunks.removeIf(chunk -> chunk < minChunk || chunk > maxChunk);
    }

    /**
     * Checks if a GameObject is within the bounds of the current chunks.
     *
     * @param obj       The GameObject to check.
     * @param minChunk  The minimum chunk index.
     * @param maxChunk  The maximum chunk index.
     * @return true if the object is within bounds, false otherwise.
     */
    private boolean checkIfInBounds(GameObject obj, int minChunk, int maxChunk) {
        Vector2 position = obj.getCenter();
        int chunkX = (int) Math.floor(position.x() / blockSize);
        return chunkX >= minChunk -2 && chunkX <= maxChunk + 2 ;
    }

}
