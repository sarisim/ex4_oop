package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Block;
import pepse.world.Terrain;
import pepse.world.UI.EneregyUi;

import java.util.List;

public class PepseGameManager extends GameManager {

    private final float TEST = 0.5f;
    private static final float NIGHT_CYCLE_LENGTH = 30;
    private static final float DAY_CYCLE_LENGTH = 60;

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
//        GameObject sunHalo = pepse.world.daynight.SunHalo.create(sun);
//        gameObjects().addGameObject(sunHalo, Layer.BACKGROUND);
        //TODO figure out how to spawn the avatar at the correct height
        Avatar avatar =new Avatar(windowController.getWindowDimensions().mult(TEST),
                inputListener,imageReader);
        gameObjects().addGameObject(avatar,Layer.DEFAULT);

            EneregyUi energyText = new EneregyUi(Vector2.ZERO,new Vector2(30,30),
                avatar::getEnergy);
        gameObjects().addGameObject(energyText,Layer.BACKGROUND);
    }


    public static void main(String[] args) {
        new PepseGameManager().run();

    }
}
