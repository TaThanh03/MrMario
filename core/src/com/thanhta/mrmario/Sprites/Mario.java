package com.thanhta.mrmario.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.thanhta.mrmario.MrMario;
import com.thanhta.mrmario.Screens.PlayScreen;

public class Mario extends Sprite {
    public enum State {FALLING, JUMPING, STANDING, RUNNING}
    public State currentState;
    public State previousState;

    public World world;
    public Body b2body;
    private TextureRegion marioStand;

    private Animation marioRun;
    private Animation marioJump;
    private float stateTimer;
    private boolean runningRight;

    public Mario (PlayScreen screen){
        super(screen.getAtlas().findRegion("little_mario"));
        this.world = screen.getWorld();
        currentState  = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i=1; i<4; i++)
            frames.add(new TextureRegion(getTexture(), i*16, 0, 16, 18));
        marioRun = new Animation(0.1f, frames);
        frames.clear();
        for (int i=4; i<6; i++)
            frames.add(new TextureRegion(getTexture(), i*16, 0, 16, 18));
        marioJump = new Animation(0.1f, frames);
        marioStand = new TextureRegion(getTexture(),0,0,16,18);

        defineMario();
        setBounds(0,0,16/ MrMario.PPM, 18/MrMario.PPM);
        setRegion(marioStand);
    }
    public void update(float dt){
        setPosition(b2body.getPosition().x- getWidth()/2, b2body.getPosition().y -getHeight()/2);
        setRegion(getFrame(dt));
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();
        TextureRegion region ;
        switch (currentState){
            case JUMPING:
                region = marioJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioStand;
                break;
        }
        if ((b2body.getLinearVelocity().x <0 || !runningRight) && !region.isFlipX()){
            region.flip(true,false);
            runningRight = false;
        }
        else if ((b2body.getLinearVelocity().x >0 || runningRight) && region.isFlipX()){
            region.flip(true,false);
            runningRight = true;
        }
        //if currentState == previousState then stateTimer += dt, else stateTimer =0
        //        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        if (currentState == previousState){ stateTimer += dt;}
        else {stateTimer=0;}
        //update previous state
        previousState = currentState;
        return region;
    }

    public State getState() {
        if (b2body.getLinearVelocity().y >0 || (b2body.getLinearVelocity().y <0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if (b2body.getLinearVelocity().y <0)
            return State.FALLING;
        else if (b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }
    public void defineMario() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(32/ MrMario.PPM,32/ MrMario.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        //know what bit is mario
        fixtureDef.filter.categoryBits = MrMario.MARIO_BIT;
        //what fixture can mario collide?
        fixtureDef.filter.maskBits = MrMario.GROUND_BIT | MrMario.BRICK_BIT
                | MrMario.COIN_BIT | MrMario.ENEMY_BIT | MrMario.OBJECT_BIT;


        //create mario's body
        CircleShape shape = new CircleShape();
        shape.setRadius(7/ MrMario.PPM);
        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef);
        //create mario's head sensor
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2/ MrMario.PPM, 8/ MrMario.PPM),new Vector2(2/ MrMario.PPM, 8/ MrMario.PPM));
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        b2body.createFixture(fixtureDef).setUserData("head");
    }
}
