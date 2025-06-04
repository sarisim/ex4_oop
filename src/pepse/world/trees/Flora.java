
package pepse.world.trees;

import danogl.util.Vector2;

import java.util.*;
import java.util.function.Function;

/**
 * Flora class is responsible for generating trees in the game world.
 * It uses a height function to determine the ground level and places trees based on a random seed.
 */
public class Flora {
    private final int seed;
    private final Function<Float, Float> heightFunc;

    /**
     * Constructs a Flora instance with a specified height function and seed.
     *
     * @param heightFunc Function to determine the ground level at a given x-coordinate.
     * @param seed       Seed for random number generation to ensure consistent tree placement.
     */
    public Flora(Function<Float, Float> heightFunc, int seed) {
        this.heightFunc = heightFunc;
        this.seed = seed;
    }

    /**
     * Creates a list of trees within a specified x-coordinate range.
     *
     * @param minX Minimum x-coordinate for tree placement.
     * @param maxX Maximum x-coordinate for tree placement.
     * @return List of trees created within the specified range.
     */
    public List<Tree> createInRange(int minX, int maxX) {
        List<Tree> trees = new ArrayList<>();
        for (float x = minX; x <= maxX; x += 50) {
            if (shouldPlaceTree(x)) {
                float groundY = heightFunc.apply(x);
                trees.add(createTree(new Vector2(x, groundY), x));
            }
        }
        return trees;
    }

    private Tree createTree(Vector2 location, float xSeedSource) {
        //create seed based on x coordinate and global seed
        int treeSeed = Float.hashCode(xSeedSource) ^ seed;
        Random rand = new Random(treeSeed);

        // Generate random dimensions for the tree components
        int trunkHeight = rand.nextInt(150) + 50;
        int trunkWidth = rand.nextInt(10) + 10;
        int leafsLength = rand.nextInt(50) + 100;

        // Calculate the top-left position of the trunk
        Vector2 trunkTopLeft = new Vector2(location.x(), location.y() - trunkHeight);
        // Create the trunk object
        Trunk trunk = new Trunk(trunkTopLeft, new Vector2(trunkWidth, trunkHeight));
        trunk.setTag("trunk");

        // Initialize lists for leaves and fruits
        List<Leaf> leaves = new ArrayList<>();
        List<Fruit> fruits = new ArrayList<>();

        // Generate leaves and fruits based on the leafsLength
        for (int i = 0; i < leafsLength; i += Leaf.LEAF_LENGTH) {
            for (int j = 0; j < leafsLength; j += Leaf.LEAF_LENGTH) {
                Vector2 leafLocation = new Vector2(
                        trunkTopLeft.x() + (trunkWidth / 2) + (i - leafsLength / 2),
                        trunkTopLeft.y() - (j + Leaf.LEAF_LENGTH)
                );
                if (rand.nextDouble() < 0.8) {
                    Leaf leaf = Leaf.createLeaf(leafLocation);
                    leaf.setTag("leaf");
                    leaves.add(leaf);

                    if (rand.nextDouble() < 0.05) {
                        Fruit fruit = new Fruit(leafLocation);
                        fruit.setTag("fruit");
                        fruits.add(fruit);
                    }
                }
            }
        }

        return new Tree(trunk, leaves, fruits, trunkHeight + leafsLength);
    }

    /**
     * Determines whether a tree should be placed at a given x-coordinate based on a random seed.
     *
     * @param xSeedSource The x-coordinate used to generate the seed for tree placement.
     * @return true if a tree should be placed, false otherwise.
     */
    private boolean shouldPlaceTree(float xSeedSource) {
        int treeSeed = Float.hashCode(xSeedSource) ^ seed;
        Random rand = new Random(treeSeed);
        return rand.nextDouble() < 0.1;
    }
}


//*TODO NEWEST CODE WITH NOISE GENERATOR
//
//package pepse.world.trees;
//
//import danogl.util.Vector2;
//import pepse.util.NoiseGenerator;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.Function;
//
//public class Flora {
//    private static final float TREE_NOISE_FACTOR = 80f; // לא קטן מדי
//    private final Function<Float, Float> heightFunc;
//    private final NoiseGenerator structureNoise;
//    private final NoiseGenerator detailNoise;
//
//    public Flora(Function<Float, Float> heightFunc, int seed) {
//        this.heightFunc = heightFunc;
//        this.structureNoise = new NoiseGenerator(seed, 0);       // מגדיר מיקום עצים
//        this.detailNoise = new NoiseGenerator(seed + 1, 0);      // מגדיר וריאציות בעץ
//    }
//
//    public List<Tree> createInRange(int minX, int maxX) {
//        List<Tree> trees = new ArrayList<>();
//        for (float x = minX; x <= maxX; x += 50) {
//            if (shouldPlaceTree(x)) {
//                float groundY = heightFunc.apply(x);
//                trees.add(createTree(new Vector2(x, groundY), x));
//            }
//        }
//        return trees;
//    }
//
//    private boolean shouldPlaceTree(float x) {
//        float noiseVal = structureNoise.noise(x, TREE_NOISE_FACTOR);
//        float normalized = (noiseVal + 1f) / 2f; // לטווח [0,1]
//        return normalized > 0.85; // ככל שקרוב ל-1, פחות עצים – תתחיל מ-0.85
//    }
//
//    private Tree createTree(Vector2 location, float xSeedSource) {
//        int treeSeed = Float.hashCode(xSeedSource); // מייצר שונות
//        java.util.Random rand = new java.util.Random(treeSeed);
//
//        int trunkHeight = rand.nextInt(150) + 50;
//        int trunkWidth = rand.nextInt(10) + 10;
//        int leafsLength = rand.nextInt(50) + 100;
//
//        Vector2 trunkTopLeft = new Vector2(location.x(), location.y() - trunkHeight);
//        Trunk trunk = new Trunk(trunkTopLeft, new Vector2(trunkWidth, trunkHeight));
//        trunk.setTag("trunk");
//
//        List<Leaf> leaves = new ArrayList<>();
//        List<Fruit> fruits = new ArrayList<>();
//
//        for (int i = 0; i < leafsLength; i += Leaf.LEAF_LENGTH) {
//            for (int j = 0; j < leafsLength; j += Leaf.LEAF_LENGTH) {
//                Vector2 leafLoc = new Vector2(
//                        trunkTopLeft.x() + (trunkWidth / 2) + (i - leafsLength / 2),
//                        trunkTopLeft.y() - (j + Leaf.LEAF_LENGTH)
//                );
//                if (rand.nextDouble() < 0.8) {
//                    Leaf leaf = Leaf.createLeaf(leafLoc);
//                    leaf.setTag("leaf");
//                    leaves.add(leaf);
//
//                    if (rand.nextDouble() < 0.05) {
//                        Fruit fruit = new Fruit(leafLoc);
//                        fruit.setTag("fruit");
//                        fruits.add(fruit);
//                    }
//                }
//            }
//        }
//
//        return new Tree(trunk, leaves, fruits, trunkHeight + leafsLength);
//    }
//}
