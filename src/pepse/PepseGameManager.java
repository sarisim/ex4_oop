package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Block;
import pepse.world.Terrain;
import pepse.world.UI.EneregyUi;
import pepse.world.daynight.SkyManager;
import pepse.world.daynight.Sun;
import pepse.world.trees.*;
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
    Avatar avatar;
    Terrain terrain;
    Flora flora;
    GameObject sun;
    private Vector2 windowDims;
    SkyManager skyManager;
    EndlessWorldManager endlessWorldManager;

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowController.setTargetFramerate(60);
        this.windowDims = windowController.getWindowDimensions();
        createSky();
        createNight();
        this.sun = createSunAndHalo();
        int avatarInitialX = (int) (windowDims.x() / 2f);
        this.terrain = new Terrain(windowDims, 1234);
        int avatarInitialY = (int) (terrain.groundHeightAt(avatarInitialX) - Avatar.AVATAR_SIZE);

        createAvatar(avatarInitialX, avatarInitialY, inputListener, imageReader);
        createEnergyUi(avatar);
        Flora flora = new Flora(terrain::groundHeightAt, 42);
        this.flora = flora;
//        Tree tree = flora.createTree(new Vector2(200,terrain.groundHeightAt(200)));
//        List<Tree> trees = flora.createInRange(0,(int)windowController.getWindowDimensions().x(),
//                gameObjects()::addGameObject);
//        for (Tree tree : trees) {
//            gameObjects().addGameObject(tree.getTrunk(),Layer.DEFAULT);
//            for (Leaf leaf : tree.getLeaves()) {
//                gameObjects().addGameObject(leaf,Layer.STATIC_OBJECTS);
//            }
//            for (Fruit fruit : tree.getFruits()){
//                gameObjects().addGameObject(fruit,Layer.DEFAULT);
//            }
//        }

        Vector2 initialAvatarLocation = new Vector2(avatar.getTopLeftCorner().x(),
                terrain.groundHeightAt(avatar.getTopLeftCorner().x()) - Avatar.AVATAR_SIZE);
        Vector2 cameraPosition = new Vector2(windowController.getWindowDimensions().mult(0.5f));
        cameraPosition = cameraPosition.subtract(initialAvatarLocation);
        Camera camera = new Camera(avatar, cameraPosition,
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions());
        setCamera(camera);
        Cloud cloud = new Cloud(windowDims, SIZE_CLOUD);
        List<CloudBlock> cloudBlocks = cloud.getBlocks();
        for (CloudBlock block : cloudBlocks) {
            gameObjects().addGameObject(block, Layer.BACKGROUND);
        }
        Rain rain = new Rain(gameObjects()::addGameObject, gameObjects()::removeGameObject,
                cloud, imageReader, camera);
        avatar.registerJumpObserver(rain);

        SkyManager skyManager = new SkyManager(sun, cloud);
        this.skyManager = skyManager;

        endlessWorldManager = new EndlessWorldManager(terrain, flora,
                gameObjects()::addGameObject, gameObjects()::removeGameObject,
                avatar, windowDims);
        endlessWorldManager.update(0);


    }

    private void createEnergyUi(Avatar avatar) {
        //TODO temp size for energy text, figure out what to do
        EneregyUi energyText = new EneregyUi(Vector2.ZERO,new Vector2(30,30),
                avatar::getEnergy);
        gameObjects().addGameObject(energyText,Layer.UI);

    }

    private void createAvatar(int avatarInitialX,
                              int avatarInitialY,
                              UserInputListener inputListener,
                              ImageReader imageReader) {
        Avatar avatar =new Avatar(new Vector2(avatarInitialX, avatarInitialY),
                inputListener,imageReader);
        this.avatar = avatar;
        avatar.setTag("avatar");
        gameObjects().addGameObject(avatar,Layer.DEFAULT);

    }

    private void createTerrain(int avatarInitialX) {
//        List<Block> blocks = terrain.createInRange(0, (int) windowDims.x());
//        for (Block block : blocks) {
//            gameObjects().addGameObject(block, Layer.DEFAULT);
//        }
//        this.terrain = terrain;
//        terrain.updateTerrainRange(
//                avatarInitialX,
//                (int) windowDims.x(),
//                gameObjects()::addGameObject,
//                gameObjects()::removeGameObject
//        );
    }

    private GameObject createSunAndHalo() {
        GameObject sun = pepse.world.daynight.Sun.create(windowDims, DAY_CYCLE_LENGTH);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);
        GameObject sunHalo = pepse.world.daynight.SunHalo.create(sun);
        gameObjects().addGameObject(sunHalo, Layer.BACKGROUND);
        return sun;


    }

    private void createNight() {
        GameObject night = pepse.world.daynight.Night.create(windowDims, NIGHT_CYCLE_LENGTH);
        gameObjects().addGameObject(night, Layer.BACKGROUND);

    }

    private void createSky() {
        GameObject sky = pepse.world.Sky.create(windowDims);
        gameObjects().addGameObject(sky, Layer.BACKGROUND);

    }

    public static void main(String[] args) {
        new PepseGameManager().run();

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        endlessWorldManager.update(deltaTime);


    }
}
