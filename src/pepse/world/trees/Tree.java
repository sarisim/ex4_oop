package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.List;
import java.util.Random;

public class Tree {


    private final Trunk trunk;
    private final List<Leaf> leaves;
    private final int treeHeight;

    public Tree(Trunk trunk, List<Leaf> leaves,int treeHeight) {
        this.trunk = trunk;
        this.leaves = leaves;
        this.treeHeight = treeHeight;
    }

    public List<Leaf> getLeaves() {
        return leaves;
    }

    public Trunk getTrunk() {
        return trunk;
    }

    public int getTreeHeight() {
        return treeHeight;
    }
}
