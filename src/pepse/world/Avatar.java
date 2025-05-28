package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

public class Avatar extends GameObject {
    private static final float GRAVITY = 600;
    private static final float VELOCITY_X = 400;
    private static final float VELOCITY_Y = -650;
    private float energy;
    private enum State {
        IDLE, RUN, JUMP
    }
    private final AnimationRenderable IDLE_ANIMATION;
    private AnimationRenderable RUN_ANIMATION;
    private AnimationRenderable JUMP_ANIMATION;
    private Boolean walkingDirection = true;


    private final UserInputListener inputListener;

    public Avatar(Vector2 topLeftCorner,
                  UserInputListener inputListener,
                  ImageReader imageReader){
        super(topLeftCorner,Vector2.ONES.mult(50),
                imageReader.readImage("assets/idle_0.png",true));
        transform().setAccelerationY(GRAVITY);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        this.inputListener = inputListener;
        this.energy = 100;
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
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT))
            xVel -= VELOCITY_X;
        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT))
            xVel += VELOCITY_X;
        if (xVel > 0 && updateEnergy(State.RUN)){
            transform().setVelocityX(xVel);
            renderer().setIsFlippedHorizontally(walkingDirection);
            renderer().setRenderable(RUN_ANIMATION);
            walkingDirection = false;
        }
        else if (xVel < 0 && updateEnergy(State.RUN)){
            transform().setVelocityX(xVel);
            renderer().setRenderable(RUN_ANIMATION);
            renderer().setIsFlippedHorizontally(walkingDirection);
            walkingDirection = true;
        }
        else {
            transform().setVelocityX(0);
        }
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0) {
            if(updateEnergy(State.JUMP)){
                transform().setVelocityY(VELOCITY_Y);}
                renderer().setRenderable(JUMP_ANIMATION);
        }
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
    }

    public float getEnergy() {
        return energy;
    }
    private boolean updateEnergy(State state) {
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
                return true;
            default:
                return false;
        }
    }
}