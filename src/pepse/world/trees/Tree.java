package pepse.world.trees;


import java.util.List;

/**
 * Represents a tree in the game world.
 * A tree consists of a trunk, leaves, and fruits.
 */
public class Tree {

    private final Trunk trunk;
    private final List<Leaf> leaves;
    private final List<Fruit> fruits;
    private final int treeHeight;
    /**
     * Constructs a new Tree instance.
     *
     * @param trunk       The trunk of the tree.
     * @param leaves      The leaves of the tree.
     * @param fruits      The fruits of the tree.
     * @param treeHeight  The height of the tree.
     */
    public Tree(Trunk trunk, List<Leaf> leaves, List<Fruit> fruits, int treeHeight) {
        this.trunk = trunk;
        this.leaves = leaves;
        this.treeHeight = treeHeight;
        this.fruits = fruits;
    }
    /**
     * Gets the height of the tree.
     *
     * @return The height of the tree.
     */
    public List<Leaf> getLeaves() {
        return leaves;
    }

    /**
     * Gets the height of the tree.
     *
     * @return The height of the tree.
     */
    public Trunk getTrunk() {
        return trunk;
    }

    /**
     * Gets the height of the tree.
     *
     * @return The height of the tree.
     */
    public List<Fruit> getFruits() {
        return fruits;
    }
}
