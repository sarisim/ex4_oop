//package pepse.world.trees;
//
//import danogl.GameObject;
//import danogl.collisions.Layer;
//import danogl.util.Vector2;
//import pepse.world.Block;
//
//import java.awt.*;
//import java.util.*;
//import java.util.List;
//import java.util.function.BiConsumer;
//import java.util.function.Function;
//import java.util.function.IntFunction;
//
//public class Flora {
//    private final Random rand = new Random(42);
//    private final Function<Float, Float> heightFunc;
//    private int minX;
//    private int maxX;
//
////    private final IntFunction<Float> groundHeightFunc;
////    private final int seed;
////    private final Map<Integer, List<Tree>> treesByChunk = new HashMap<>();
////    private final int chunkSize = 50;
//
//
//    public Flora(Function<Float, Float> heightFunc) {
//        this.heightFunc = heightFunc;
//    }
//
////    public Flora(IntFunction<Float> groundHeightFunc, int seed) {
////        this.groundHeightFunc = groundHeightFunc;
////        this.seed = seed;
////    }
//
////    private List<Tree> generateChunkTrees(int chunkId) {
////        List<Tree> trees = new ArrayList<>();
////
////        int startX = chunkId * chunkSize;
////        int endX = startX + chunkSize;
////
////        for (int x = startX; x < endX; x += 15) {
////            if (rand.nextFloat() < 0.1f) { // 10% chance of tree
////                float groundY = groundHeightFunc.apply(x);
////                trees.add(createTree(new Vector2(x, groundY)));
////            }
////        }
////        return trees;
////    }
//
////    public void updateTrees(int avatarX, int viewWidth,
////                            BiConsumer<GameObject, Integer> addFunc,
////                            BiConsumer<GameObject, Integer> removeFunc) {
////        int minChunk = (avatarX - viewWidth * 2) / chunkSize;
////        int maxChunk = (avatarX + viewWidth * 2) / chunkSize;
////
////        Set<Integer> currentChunks = new HashSet<>(treesByChunk.keySet());
////
////        for (int chunk = minChunk; chunk <= maxChunk; chunk++) {
////            if (!treesByChunk.containsKey(chunk)) {
////                List<Tree> trees = generateChunkTrees(chunk);
////                treesByChunk.put(chunk, trees);
////                for (Tree t : trees) {
////                    addFunc.accept(t.getTrunk(), Layer.DEFAULT);
////                    t.getLeaves().forEach(leaf -> addFunc.accept(leaf, Layer.STATIC_OBJECTS));
////                    t.getFruits().forEach(fruit -> addFunc.accept(fruit, Layer.DEFAULT));
////                }
////            }
////            currentChunks.remove(chunk);
////        }
////
////        // Remove far-away chunks
////        for (int chunkToRemove : currentChunks) {
////            List<Tree> trees = treesByChunk.remove(chunkToRemove);
////            if (trees != null) {
////                for (Tree t : trees) {
////                    removeFunc.accept(t.getTrunk(), Layer.DEFAULT);
////                    t.getLeaves().forEach(leaf -> removeFunc.accept(leaf, Layer.STATIC_OBJECTS));
////                    t.getFruits().forEach(fruit -> removeFunc.accept(fruit, Layer.DEFAULT));
////                }
////            }
////        }
////    }
//
//
//
//    public List<Tree> createInRange(int minX, int maxX){
//        List<Tree> trees = new ArrayList<>();
//        for (float x = minX; x <= maxX; x+= 50){
//            if (treeOrNotToTree()){
//                Tree tree = createTree(new Vector2(x,heightFunc.apply(x)));
//                trees.add(tree);
//            }
//        }
////        for (Tree tree : trees) {
////            gameObjectAdder.accept(tree.getTrunk(), Layer.DEFAULT);
////            for (Leaf leaf : tree.getLeaves()) {
////                gameObjectAdder.accept(leaf, Layer.STATIC_OBJECTS);
////            }
////            for (Fruit fruit : tree.getFruits()) {
////                gameObjectAdder.accept(fruit, Layer.DEFAULT);
////            }
////        }
//        return trees;
//    }
//
//    private Tree createTree(Vector2 location){
//        int trunkHeight = rand.nextInt(150)+50;
//        int trunkWidth = rand.nextInt(10) +10;
//        int leafsLength = rand.nextInt(50)+100;
//
//        int treeHeight = trunkHeight+leafsLength;
//        Vector2 test = new Vector2(
//                location.x(),location.y()-trunkHeight
//        );
//
//        Trunk trunk = new Trunk(test,
//                new Vector2(trunkWidth,trunkHeight));
//        trunk.setTag("trunk");
//
//        List<Fruit> fruits = new ArrayList<>();
//        List<Leaf> leaves = new ArrayList<>();
//
//
//
//        for (int i = 0; i < leafsLength ; i += Leaf.LEAF_LENGTH) {
//            for (int j = 0; j <leafsLength ; j += Leaf.LEAF_LENGTH) {
//                Vector2 leafLocation = new Vector2(test.x() + (trunkWidth / 2) + (i - leafsLength / 2),
//                        test.y() - ((j+Leaf.LEAF_LENGTH)));
//                if (LeafOrNotToLEAF()){
//                    Leaf leaf =Leaf.createLeaf(leafLocation);
//                    leaf.setTag("leaf");
//                    leaves.add(leaf);
//                }
//                if (FruitOrNotToFruit()){
//                    Fruit fruit = new Fruit(leafLocation);
//                    fruit.setTag("fruit");
//                    fruits.add(fruit);
//                }
//            }
//        }
//        return new Tree(trunk,leaves,fruits, trunkHeight+leafsLength);
//    }
//
//    private boolean FruitOrNotToFruit() {
//        return rand.nextDouble() < 0.05;
//    }
//
//    private boolean treeOrNotToTree(){
//        return rand.nextDouble() < 0.1;
//    }
//
//    private boolean LeafOrNotToLEAF(){
//        return rand.nextDouble() < 0.8;
//    }
//
//}


//* TODO OLD CODE

package pepse.world.trees;

import danogl.util.Vector2;
import java.util.*;
import java.util.function.Function;

public class Flora {
    private final int seed;
    private final Function<Float, Float> heightFunc;

    public Flora(Function<Float, Float> heightFunc, int seed) {
        this.heightFunc = heightFunc;
        this.seed = seed;
    }

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
        int treeSeed = Float.hashCode(xSeedSource) ^ seed;
        Random rand = new Random(treeSeed);

        int trunkHeight = rand.nextInt(150) + 50;
        int trunkWidth = rand.nextInt(10) + 10;
        int leafsLength = rand.nextInt(50) + 100;

        Vector2 trunkTopLeft = new Vector2(location.x(), location.y() - trunkHeight);
        Trunk trunk = new Trunk(trunkTopLeft, new Vector2(trunkWidth, trunkHeight));
        trunk.setTag("trunk");

        List<Leaf> leaves = new ArrayList<>();
        List<Fruit> fruits = new ArrayList<>();

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

    private boolean shouldPlaceTree(float xSeedSource) {
        int treeSeed = Float.hashCode(xSeedSource) ^ seed;
        Random rand = new Random(treeSeed);
        return rand.nextDouble() < 0.1;
    }
}

