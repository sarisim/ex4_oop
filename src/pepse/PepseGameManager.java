package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Terrain;
import pepse.world.UI.EneregyUi;
import pepse.world.clouds.Cloud;
import pepse.world.clouds.CloudBlock;
import pepse.world.clouds.Rain;
import pepse.world.trees.Flora;

import java.util.List;
import java.util.Random;

public class PepseGameManager extends GameManager {

    int worldSeed = new Random().nextInt();


    private static final float NIGHT_CYCLE_LENGTH = 30;
    private static final float DAY_CYCLE_LENGTH = 60;
    private static final float SIZE_CLOUD = 20;
    Avatar avatar;
    Terrain terrain;
    Flora flora;
    GameObject sun;
    private Vector2 windowDims;
    SkyManager skyManager;
    EndlessWorldManager endlessWorldManager;

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        windowController.setTargetFramerate(60);
        this.windowDims = windowController.getWindowDimensions();
        createSky();
        createNight();
        this.sun = createSunAndHalo();
        int avatarInitialX = (int) (windowDims.x() / 2f);
        this.terrain = new Terrain(windowDims, worldSeed);
        int avatarInitialY = (int) (terrain.groundHeightAt(avatarInitialX) - Avatar.AVATAR_SIZE);

        createAvatar(avatarInitialX, avatarInitialY, inputListener, imageReader);
        createEnergyUi(avatar);
        Flora flora = new Flora(terrain::groundHeightAt, worldSeed);
        this.flora = flora;

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
