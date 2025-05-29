package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;


public class Avatar extends GameObject {
    private static final float GRAVITY = 600;
    private static final float VELOCITY_X = 400;
    private static final float VELOCITY_Y = -650;
    public static final int AVATAR_SIZE = 50;
    private final Consumer<GameObject> removeFunc;
    private final Consumer<GameObject> gameObjectAdder;

    public static enum State {
        IDLE, RUN, JUMP,FRUIT
    }
    private float energy;
    private final AnimationRenderable IDLE_ANIMATION;
    private final AnimationRenderable RUN_ANIMATION;
    private final AnimationRenderable JUMP_ANIMATION;
    private Boolean walkingDirection = true;
    private final HashSet<JumpObserver> JumpObservers = new HashSet<JumpObserver>();
    private final UserInputListener inputListener;

    public Avatar(Vector2 topLeftCorner,
                  UserInputListener inputListener,
                  ImageReader imageReader,
                  Consumer<GameObject> gameObjectRemover,
                  Consumer<GameObject> gameObjectAdder){
        super(topLeftCorner,Vector2.ONES.mult(AVATAR_SIZE),
                imageReader.readImage("assets/idle_0.png",true));
        transform().setAccelerationY(GRAVITY);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        this.inputListener = inputListener;
        this.energy = 100;
        this.gameObjectAdder = gameObjectAdder;
        this.removeFunc = gameObjectRemover;
        IDLE_ANIMATION = getIdleAnimation(imageReader);
        RUN_ANIMATION = getRunAnimation(imageReader);
        JUMP_ANIMATION = getJumpAnimation(imageReader);
    }

    private AnimationRenderable getJumpAnimation(ImageReader imageReader) {
        Renderable[] clips = new Renderable[4];
        for (int i = 0; i < clips.length; i++) {
            clips[i] = imageReader.readImage("assets/jump_" + i + ".png", true);
        }
        return new AnimationRenderable(clips, 0.5);
    }

    private AnimationRenderable getRunAnimation(ImageReader imageReader) {
        Renderable[] clips = new Renderable[5];
        for (int i = 0; i < clips.length; i++) {
            clips[i] = imageReader.readImage("assets/run_" + i + ".png", true);
        }
        return new AnimationRenderable(clips, 0.5);
    }

    private AnimationRenderable getIdleAnimation(ImageReader imageReader) {
        Renderable[] clips = new Renderable[4];
        for (int i = 0; i < clips.length; i++) {
            clips[i] = imageReader.readImage("assets/idle_" + i + ".png", true);
        }
        return new AnimationRenderable(clips, 0.5);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;
        //calculate the moving direction horizontally
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT))
            xVel -= VELOCITY_X;
        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT))
            xVel += VELOCITY_X;
        //check if the player is able to move,
        if (updateEnergy(State.RUN)){
            //according to the direction, update the animation direction
            renderer().setRenderable(RUN_ANIMATION);
            if (xVel>0){
                renderer().setIsFlippedHorizontally(walkingDirection);
                walkingDirection = false;
            }
            if (xVel < 0){
                renderer().setIsFlippedHorizontally(walkingDirection);
                walkingDirection = true;
            }
            transform().setVelocityX(xVel);
        }
        // if the player don't move, set the velocity to 0
        else {
            transform().setVelocityX(0);
        }
        //jumping state
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0) {
            if(updateEnergy(State.JUMP)){
                transform().setVelocityY(VELOCITY_Y);}
                renderer().setRenderable(JUMP_ANIMATION);
            for (JumpObserver observer : this.JumpObservers){
                observer.update(true);
            }
        }
        //idle state
        if (getVelocity().y() == 0 && getVelocity().x() == 0){
            updateEnergy(State.IDLE);
            renderer().setRenderable(IDLE_ANIMATION);
        }
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(other.getTag().equals("block")){
            this.transform().setVelocityY(0);
        }
        if(other.getTag().equals("fruit")){
            updateEnergy(State.FRUIT);
            this.removeFunc.accept(other);
            new ScheduledTask(
                    other,
                    5,
                    false,
                    ()-> gameObjectAdder.accept((other))
            );
        }
    }

    public float getEnergy() {
        return energy;
    }
    public boolean updateEnergy(State state) {
        switch (state){
            case RUN:
                if (this.energy >= 0.5){
                    energy -= 0.5f;
                    return true;
                }
                else return false;
            case JUMP:
                if (this.energy >=10){
                    this.energy -= 10;
                    return true;
                }
                else return false;
            case IDLE:
                energy += 1;
                if (energy > 100){
                    energy = 100;
                }
            case FRUIT:
                energy += 10;
                if (energy > 100){
                    energy = 100;
                }
                return true;
            default:
                return false;
        }
    }
    public void registerJumpObserver(JumpObserver gameObject){
        this.JumpObservers.add(gameObject);
    }


}