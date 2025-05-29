package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Block;
import pepse.world.Terrain;
import pepse.world.UI.EneregyUi;
import pepse.world.clouds.Cloud;
import pepse.world.clouds.CloudBlock;
import pepse.world.clouds.Rain;
import pepse.world.trees.Flora;
import pepse.world.trees.Leaf;
import pepse.world.trees.Tree;
import pepse.world.trees.Trunk;

import java.util.List;

public class PepseGameManager extends GameManager {


    private static final float NIGHT_CYCLE_LENGTH = 30;
    private static final float DAY_CYCLE_LENGTH = 60;
    private static final float SIZE_CLOUD = 20;
    private final float TEST = 0.5f;

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        GameObject sky = pepse.world.Sky.create(windowController.getWindowDimensions());
        gameObjects().addGameObject(sky, Layer.BACKGROUND);

        Terrain terrain =  new Terrain(windowController.getWindowDimensions(), 1234);
        List<Block> blocks = terrain.createInRange(0, (int)windowController.getWindowDimensions().x());
        for (Block block : blocks) {
            gameObjects().addGameObject(block, Layer.DEFAULT);
        }

        GameObject night = pepse.world.daynight.Night.create(windowController.getWindowDimensions(), NIGHT_CYCLE_LENGTH);
        gameObjects().addGameObject(night, Layer.BACKGROUND);
        GameObject sun = pepse.world.daynight.Sun.create(windowController.getWindowDimensions(), DAY_CYCLE_LENGTH);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);
        GameObject sunHalo = pepse.world.daynight.SunHalo.create(sun);
        gameObjects().addGameObject(sunHalo, Layer.BACKGROUND);

        Avatar avatar =new Avatar(new Vector2(0, terrain.groundHeightAt(0)-Avatar.AVATAR_SIZE),
                inputListener,imageReader);
        gameObjects().addGameObject(avatar,Layer.DEFAULT);

        //TODO temp size for energy text, figure out what to do
        EneregyUi energyText = new EneregyUi(Vector2.ZERO,new Vector2(30,30),
                avatar::getEnergy);
        gameObjects().addGameObject(energyText,Layer.UI);

        Flora flora = new Flora(terrain::groundHeightAt);
//        Tree tree = flora.createTree(new Vector2(200,terrain.groundHeightAt(200)));
        List<Tree> trees = flora.createInRange(0,(int)windowController.getWindowDimensions().x());
        for (Tree tree : trees) {
            gameObjects().addGameObject(tree.getTrunk(),Layer.DEFAULT);
            for (Leaf leaf : tree.getLeaves()) {
                gameObjects().addGameObject(leaf,Layer.STATIC_OBJECTS);
            }
        }

        Cloud cloud = new Cloud(windowController.getWindowDimensions(), SIZE_CLOUD);
        List<CloudBlock> cloudBlocks = cloud.getBlocks();
        for (CloudBlock block : cloudBlocks) {
            gameObjects().addGameObject(block, Layer.BACKGROUND);
        }

        Rain rain = new Rain(this, cloud, imageReader);
        avatar.registerJumpObserver(rain);

    }

    public static void main(String[] args) {
        new PepseGameManager().run();

    }

    public void addObject(GameObject object, int background) {
        gameObjects().addGameObject(object, background);
    }

    public void removeObject(GameObject object) {
        gameObjects().removeGameObject(object);
    }
}
