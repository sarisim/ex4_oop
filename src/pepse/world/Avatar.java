package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.world.trees.Fruit;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class Avatar extends GameObject {
    private static final float GRAVITY = 600;
    private static final float VELOCITY_X = 400;
    private static final float VELOCITY_Y = -650;
    public static final int AVATAR_SIZE = 50;
    public static final int MAX_ENERGY = 100;
    public static final double TIME_BETWEEN_CLIPS = 0.5;
    public static final int MIN_ENERGY_TO_JUMP = 10;
    public static final double IDLE_REGEN_RATE = 0.5;
    public static final float MIN_EMERGY_TO_RUN = 0.5f;

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
                  ImageReader imageReader){
        super(topLeftCorner,Vector2.ONES.mult(AVATAR_SIZE),
                imageReader.readImage("assets/idle_0.png",true));
        transform().setAccelerationY(GRAVITY);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        this.inputListener = inputListener;
        this.energy = MAX_ENERGY;
        IDLE_ANIMATION = getIdleAnimation(imageReader);
        RUN_ANIMATION = getRunAnimation(imageReader);
        JUMP_ANIMATION = getJumpAnimation(imageReader);
    }

    private AnimationRenderable getJumpAnimation(ImageReader imageReader) {
        Renderable[] clips = new Renderable[4];
        for (int i = 0; i < clips.length; i++) {
            clips[i] = imageReader.readImage("assets/jump_" + i + ".png", true);
        }
        return new AnimationRenderable(clips, TIME_BETWEEN_CLIPS);
    }

    private AnimationRenderable getRunAnimation(ImageReader imageReader) {
        Renderable[] clips = new Renderable[5];
        for (int i = 0; i < clips.length; i++) {
            clips[i] = imageReader.readImage("assets/run_" + i + ".png", true);
        }
        return new AnimationRenderable(clips, TIME_BETWEEN_CLIPS);
    }

    private AnimationRenderable getIdleAnimation(ImageReader imageReader) {
        Renderable[] clips = new Renderable[4];
        for (int i = 0; i < clips.length; i++) {
            clips[i] = imageReader.readImage("assets/idle_" + i + ".png", true);
        }
        return new AnimationRenderable(clips, TIME_BETWEEN_CLIPS);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;

        // Determine horizontal input
        boolean left = inputListener.isKeyPressed(KeyEvent.VK_LEFT);
        boolean right = inputListener.isKeyPressed(KeyEvent.VK_RIGHT);
        boolean moved = false;

        if (left) xVel -= VELOCITY_X;
        if (right) xVel += VELOCITY_X;

        // Handle running movement if energy allows
        if (xVel != 0 && updateEnergy(State.RUN)) {
            transform().setVelocityX(xVel);
            renderer().setRenderable(RUN_ANIMATION);
            renderer().setIsFlippedHorizontally(xVel < 0);
            walkingDirection = xVel < 0;
            moved = true;
        } else {
            transform().setVelocityX(0);
        }

        // Handle jumping
        boolean isOnGround = getVelocity().y() == 0;
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && isOnGround) {
            if (updateEnergy(State.JUMP)) {
                transform().setVelocityY(VELOCITY_Y);
                renderer().setRenderable(JUMP_ANIMATION);
                for (JumpObserver observer : JumpObservers) {
                    observer.update(true);
                }
            }
        }

        // Handle idle state
        boolean isIdle = isOnGround && getVelocity().x() == 0;
        if (isIdle && !moved) {
            updateEnergy(State.IDLE);
            renderer().setRenderable(IDLE_ANIMATION);
        }
    }

//    @Override
//    public void update(float deltaTime) {
//        super.update(deltaTime);
//        float xVel = 0;
//        //calculate the moving direction horizontally
//        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT))
//            xVel -= VELOCITY_X;
//        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT))
//            xVel += VELOCITY_X;
//        //check if the player is able to move,
//        if (xVel != 0){
//            if (updateEnergy(State.RUN)){
//                //according to the direction, update the animation direction
//                renderer().setRenderable(RUN_ANIMATION);
//                if (xVel>0){
//                    renderer().setIsFlippedHorizontally(walkingDirection);
//                    walkingDirection = false;
//                }
//                if (xVel < 0){
//                    renderer().setIsFlippedHorizontally(walkingDirection);
//                    walkingDirection = true;
//                }
//                transform().setVelocityX(xVel);
//            } //*TODO I ADDED HERE THE TRANSFORM ELSE BC HE WAS MOVING WITH NO ENERGY, NEED TO CHECK
//            else {
//                transform().setVelocityX(0);
//            }
//        }
//        // if the player don't move, set the velocity to 0
//        else {
//            transform().setVelocityX(0);
//        }
//        //jumping state
//        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0) {
//            if(updateEnergy(State.JUMP)){
//                transform().setVelocityY(VELOCITY_Y);}
//                renderer().setRenderable(JUMP_ANIMATION);
//            for (JumpObserver observer : this.JumpObservers){
//                observer.update(true);
//            }
//        }
//        //idle state
//        if (getVelocity().y() == 0 && getVelocity().x() == 0){
//            updateEnergy(State.IDLE);
//            renderer().setRenderable(IDLE_ANIMATION);
//        }
//    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(other.getTag().equals("block")){
            this.transform().setVelocityY(0);
        }
        if(other.getTag().equals("fruit")){
            Fruit fruit = (Fruit) other;
            updateEnergy(State.FRUIT);
            fruit.removeAndAddFruit();
        }
    }

    public float getEnergy() {
        return energy;
    }
    public boolean updateEnergy(State state) {
        switch (state){
            case RUN:
                if (this.energy >= MIN_EMERGY_TO_RUN){
                    energy -= MIN_EMERGY_TO_RUN;
                    return true;
                }
                else return false;
            case JUMP:
                if (this.energy >= MIN_ENERGY_TO_JUMP){
                    this.energy -= MIN_ENERGY_TO_JUMP;
                    return true;
                }
                else return false;
            case IDLE:
                this.energy += IDLE_REGEN_RATE;
                if (energy > MAX_ENERGY){
                    energy = MAX_ENERGY;
                }
                return false;
            case FRUIT:
                energy += MIN_ENERGY_TO_JUMP;
                if (energy > MAX_ENERGY){
                    energy = MAX_ENERGY;
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