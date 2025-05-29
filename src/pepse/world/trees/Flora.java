package pepse.world.trees;

import danogl.GameObject;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Flora {
    private final Random rand = new Random();
    private final Function<Float, Float> heightFunc;

    public Flora(Function<Float, Float> heightFunc) {
        this.heightFunc = heightFunc;
    }

    public List<Tree> createInRange(int minX, int maxX){
        List<Tree> trees = new ArrayList<>();
        for (float x = minX; x <= maxX; x+= 50){
            if (treeOrNotToTree()){
                Tree tree =createTree(new Vector2(x,heightFunc.apply(x)));
                trees.add(tree);
            }
        }
        return trees;
    }

    private Tree createTree(Vector2 location){
        Random rand = new Random();
        int trunkHeight = rand.nextInt(150)+50;
        int trunkWidth = rand.nextInt(10) +10;
        int leafsLength = rand.nextInt(50)+100;

        int treeHeight = trunkHeight+leafsLength;
        Vector2 test = new Vector2(
                location.x(),location.y()-trunkHeight
        );

        Trunk trunk = new Trunk(test,
                new Vector2(trunkWidth,trunkHeight));
        trunk.setTag("trunk");

        List<Fruit> fruits = new ArrayList<>();
        List<Leaf> leaves = new ArrayList<>();



        for (int i = 0; i < leafsLength ; i += Leaf.LEAF_LENGTH) {
            for (int j = 0; j <leafsLength ; j += Leaf.LEAF_LENGTH) {
                Vector2 leafLocation = new Vector2(test.x() + (trunkWidth / 2) + (i - leafsLength / 2),
                        test.y() - ((j+Leaf.LEAF_LENGTH)));
                if (LeafOrNotToLEAF()){
                    Leaf leaf =Leaf.createLeaf(leafLocation);
                    leaf.setTag("leaf");
                    leaves.add(leaf);
                }
                if (FruitOrNotToFruit()){
                    Fruit fruit = new Fruit(leafLocation);
                    fruit.setTag("fruit");
                    fruits.add(fruit);
                }
            }
        }
        return new Tree(trunk,leaves,fruits, trunkHeight+leafsLength);
    }

    private boolean FruitOrNotToFruit() {
        return rand.nextDouble() < 0.05;
    }

    private boolean treeOrNotToTree(){
        return rand.nextDouble() < 0.1;
    }

    private boolean LeafOrNotToLEAF(){
        return rand.nextDouble() < 0.8;
    }

    public void updateTrees(int avatarX, int x, BiConsumer<GameObject, Integer> gameObjectAdder,
                            BiConsumer<GameObject, Integer> gameObjectRemover) {


    }
}
