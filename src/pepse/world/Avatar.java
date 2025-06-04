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
    /* * The size of the avatar in pixels.
     * This is used to determine the size of the avatar's bounding box and its appearance.
     */
    public static final int AVATAR_SIZE = 50;
    /* * The maximum energy level of the avatar.
     * This is used to determine how much energy the avatar can expend on actions like running and jumping.
     */
    public static final int MAX_ENERGY = 100;
    /* * The time between animation clips in seconds.
     * This is used to control the speed of the avatar's animations.
     */
    public static final double TIME_BETWEEN_CLIPS = 0.5;
    /* * The minimum energy required for the avatar to jump.
     * This is used to prevent the avatar from jumping if it doesn't have enough energy.
     */
    public static final int MIN_ENERGY_TO_JUMP = 10;
    /* * The rate at which the avatar regenerates energy while idle.
     * This is used to allow the avatar to regain energy over time when not performing actions.
     */
    public static final double IDLE_REGEN_RATE = 1.0f;
    /* * The minimum energy required for the avatar to run.
     * This is used to prevent the avatar from running if it doesn't have enough energy.
     */
    public static final float MIN_EMERGY_TO_RUN = 0.5f;

    // Enum for Avatar states
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

    /**
     * Constructs a new Avatar instance.
     *
     * @param topLeftCorner The top-left corner position of the Avatar in window coordinates (pixels).
     * @param inputListener The UserInputListener to handle user inputs.
     * @param imageReader   The ImageReader to read images for animations.
     */
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

    /**
     * Creates the jump animation for the Avatar.
     * The jump animation consists of 3 frames,
     * each representing a different pose of the avatar during a jump.
     *
     * @param imageReader The ImageReader to read images for the jump animation.
     * @return An AnimationRenderable containing the jump animation frames.
     */
    private AnimationRenderable getJumpAnimation(ImageReader imageReader) {
        Renderable[] clips = new Renderable[4];
        for (int i = 0; i < clips.length; i++) {
            clips[i] = imageReader.readImage("assets/jump_" + i + ".png", true);
        }
        return new AnimationRenderable(clips, TIME_BETWEEN_CLIPS);
    }

    /**
     * Creates the run animation for the Avatar.
     * The run animation consists of 5 frames, each representing a different running pose of the avatar.
     *
     * @param imageReader The ImageReader to read images for the run animation.
     * @return An AnimationRenderable containing the run animation frames.
     */
    private AnimationRenderable getRunAnimation(ImageReader imageReader) {
        Renderable[] clips = new Renderable[5];
        for (int i = 0; i < clips.length; i++) {
            clips[i] = imageReader.readImage("assets/run_" + i + ".png", true);
        }
        return new AnimationRenderable(clips, TIME_BETWEEN_CLIPS);
    }

    /**
     * Creates the idle animation for the Avatar.
     * The idle animation consists of 4 frames, each representing a different pose of the avatar.
     *
     * @param imageReader The ImageReader to read images for the idle animation.
     * @return An AnimationRenderable containing the idle animation frames.
     */
    private AnimationRenderable getIdleAnimation(ImageReader imageReader) {
        Renderable[] clips = new Renderable[4];
        for (int i = 0; i < clips.length; i++) {
            clips[i] = imageReader.readImage("assets/idle_" + i + ".png", true);
        }
        return new AnimationRenderable(clips, TIME_BETWEEN_CLIPS);
    }

    /**
     * Initializes the Avatar by setting its initial renderable and adding it to the game.
     *
     * @param deltaTime differnece in time since the last frame, in seconds.
     */
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

    /**
     * Handles collision events with other GameObjects.
     * If the avatar collides with a block, it stops vertical movement.
     * If it collides with a fruit, it updates energy and removes the fruit.
     *
     * @param other The other GameObject involved in the collision.
     * @param collision The Collision object containing collision details.
     */
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
    /**
     * Returns the current energy level of the avatar.
     *
     * @return The current energy level as a float.
     */
    public float getEnergy() {
        return energy;
    }

    /**
     * Updates the energy level of the avatar based on the current state.
     * The energy is consumed or regenerated depending on the action performed.
     *
     * @param state The current state of the avatar (RUN, JUMP, IDLE, FRUIT).
     * @return true if the action was successful (energy was consumed), false otherwise.
     */
    private boolean updateEnergy(State state) {
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