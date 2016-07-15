package com.thanhta.mrmario.Sprites;

import com.badlogic.gdx.audio.Sound;
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
    public enum State {FALLING, JUMPING, STANDING, RUNNING, GROWING}
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;

    private TextureRegion marioStand;
    private TextureRegion marioJump;
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation marioRun;
    private Animation bigMarioRun;
    private Animation growMario;

    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigMario;

    public Mario (PlayScreen screen){
        this.world = screen.getWorld();
        currentState  = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;
        Array<TextureRegion> frames = new Array<TextureRegion>();
        //fixture for Mario
        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 18);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"),0,0,16,18);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0,0, 16, 32);
        //animation for Mario
        for (int i=1; i<4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i*16, 0, 16, 18));
        marioRun = new Animation(0.1f, frames);
        frames.clear();
        for (int i=1; i<4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i*16, 0, 16, 32));
        bigMarioRun = new Animation(0.1f, frames);
        frames.clear();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),240,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),240,0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0,16,32));
        growMario = new Animation(0.2f,frames);
        frames.clear();
        defineMario();
        setBounds(0,0,16/ MrMario.PPM, 18/MrMario.PPM);
        setRegion(marioStand);
    }
    public void update(float dt){
        if (marioIsBig)
            setPosition(b2body.getPosition().x- getWidth()/2, b2body.getPosition().y -getHeight()/2 - 4.5f/MrMario.PPM);
        else
            setPosition(b2body.getPosition().x- getWidth()/2, b2body.getPosition().y -getHeight()/2);
        setRegion(getFrame(dt));
        if (timeToDefineBigMario){
            defineBigMario();
        }
    }
    public TextureRegion getFrame(float dt) {
        currentState = getState();
        TextureRegion region ;
        switch (currentState){
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if (growMario.isAnimationFinished(stateTimer))
                    runGrowAnimation = false;
                break;
            case JUMPING:
                region = marioIsBig? bigMarioJump: marioJump;
                break;
            case RUNNING:
                region = marioIsBig? bigMarioRun.getKeyFrame(stateTimer,true): marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig? bigMarioStand: marioStand;
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
    public void grow(){
        runGrowAnimation = true;
        marioIsBig = true;
        timeToDefineBigMario = true;
        setBounds(getX(),getY(),getWidth(),getHeight()*2);
        MrMario.manager.get("audio/sounds/powerup.wav", Sound.class).play();
    }
    public boolean isBig(){
        return marioIsBig;
    }
    public State getState() {
        if (runGrowAnimation)
            return State.GROWING;
        else if (b2body.getLinearVelocity().y >0 || (b2body.getLinearVelocity().y <0 && previousState == State.JUMPING))
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
                | MrMario.COIN_BIT | MrMario.ENEMY_BIT | MrMario.OBJECT_BIT
                | MrMario.ENEMY_HEAD_BIT | MrMario.ITEM_BIT;
        //create mario's body
        CircleShape shape = new CircleShape();
        shape.setRadius(7/ MrMario.PPM);
        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);
        //create mario's head sensor
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2/ MrMario.PPM, 8/ MrMario.PPM),new Vector2(2/ MrMario.PPM, 8/ MrMario.PPM));
        fixtureDef.filter.categoryBits = MrMario.MARIO_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        b2body.createFixture(fixtureDef).setUserData(this);
    }
    public void defineBigMario() {
        //destroy old small body
        Vector2 currentPossition = b2body.getPosition();
        world.destroyBody(b2body);
        //create new one
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(currentPossition.add(0, 10/MrMario.PPM));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = MrMario.MARIO_BIT;
        fixtureDef.filter.maskBits = MrMario.GROUND_BIT | MrMario.BRICK_BIT
                | MrMario.COIN_BIT | MrMario.ENEMY_BIT | MrMario.OBJECT_BIT
                | MrMario.ENEMY_HEAD_BIT | MrMario.ITEM_BIT;
        //create mario's shape
        CircleShape shape = new CircleShape();
        shape.setRadius(7/ MrMario.PPM);
        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);
        shape.setPosition(new Vector2(0, -14/MrMario.PPM));
        b2body.createFixture(fixtureDef).setUserData(this);
        //create mario's head sensor
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2/ MrMario.PPM, 8/ MrMario.PPM),new Vector2(2/ MrMario.PPM, 8/ MrMario.PPM));
        fixtureDef.filter.categoryBits = MrMario.MARIO_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        b2body.createFixture(fixtureDef).setUserData(this);
        timeToDefineBigMario = false;
    }
}
