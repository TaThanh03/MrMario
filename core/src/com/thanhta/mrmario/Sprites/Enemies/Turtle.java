package com.thanhta.mrmario.Sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.thanhta.mrmario.MrMario;
import com.thanhta.mrmario.Screens.PlayScreen;
import com.thanhta.mrmario.Sprites.Mario;
import com.thanhta.mrmario.Tools.B2WorldCreator;

public class Turtle extends Enemy {
    public static final int KICK_LEFT_SPEED= -2;
    public static final int KICK_RIGHT_SPEED= 2;
    public enum State {WALKING, STANDING_SHELL, MOVING_SHELL, DEAD}
    public State currentState;
    public State previousState;
    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private TextureRegion shell;
    private float deadRotationDegrees;
    private boolean setToDestroy;
    private boolean destroyed;

    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"),0,0,16,24));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"),16,0,16,24));
        shell = new TextureRegion(screen.getAtlas().findRegion("turtle"),16*4,0,16,24);
        walkAnimation = new Animation(0.2f,frames);
        currentState = previousState = State.WALKING;
        deadRotationDegrees=0;
        setBounds(getX(),getY(),16/ MrMario.PPM, 24/MrMario.PPM);

    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if (enemy instanceof Turtle){
            if (((Turtle) enemy).currentState == State.MOVING_SHELL && currentState != State.MOVING_SHELL){
                killed();
            }
            else if (currentState == State.MOVING_SHELL && ((Turtle) enemy).currentState == State.WALKING)
                return;
            else
                reverseVelocity(true,false);
        }
        else if (currentState != State.MOVING_SHELL)
            reverseVelocity(true,false);
    }

    @Override
    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(),getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = MrMario.ENEMY_BIT;
        fixtureDef.filter.maskBits = MrMario.GROUND_BIT | MrMario.BRICK_BIT | MrMario.MARIO_BIT
                | MrMario.COIN_BIT | MrMario.ENEMY_BIT | MrMario.OBJECT_BIT;
        CircleShape shape = new CircleShape();
        shape.setRadius(6/ MrMario.PPM);
        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);
        PolygonShape head  = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5, 8).scl(1/ MrMario.PPM);
        vertice[1] = new Vector2(5, 8).scl(1/ MrMario.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1/ MrMario.PPM);
        vertice[3] = new Vector2(3, 3).scl(1/ MrMario.PPM);
        head.set(vertice);
        fixtureDef.shape = head;
        fixtureDef.restitution = 1.2f;
        fixtureDef.filter.categoryBits = MrMario.ENEMY_HEAD_BIT;
        b2body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void hitOnHead(Mario mario) {
        if(currentState != State.STANDING_SHELL){
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        } else
            kick(mario.getX() <= this.getX()? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
    }
    public void kick(int speed){
        velocity.x = speed;
        currentState = State.MOVING_SHELL;
    }
    public State getCurrentState(){
        return currentState;
    }

    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        if (currentState == State.STANDING_SHELL && stateTime>5) {
            currentState = State.WALKING;
            velocity.x = 1;
        }
        setPosition(b2body.getPosition().x- getWidth()/2,b2body.getPosition().y - 8/MrMario.PPM);
        velocity.y = b2body.getLinearVelocity().y;
        if (currentState == State.DEAD){
            deadRotationDegrees +=1.5;
            rotate(deadRotationDegrees);
            if (stateTime >2 && !destroyed){
                world.destroyBody(b2body);
                destroyed = true;
            } else if (destroyed && stateTime>2)
                B2WorldCreator.removeEnemy(this);
        }
        else
            b2body.setLinearVelocity(velocity);
    }

    public TextureRegion getFrame(float dt) {
        TextureRegion region;
        switch (currentState){
            case STANDING_SHELL:
            case MOVING_SHELL:
                region = shell;
                break;
            case WALKING:
            default:
                region = walkAnimation.getKeyFrame(stateTime, true);
                break;
        }
        if (velocity.x >0 && region.isFlipX() ==false){
            region.flip(true,false);
        }
        if (velocity.x <0 && region.isFlipX() ==true){
            region.flip(true,false);
        }
        if (currentState == previousState){ stateTime += dt;}
        else {stateTime=0;}
        //update previous state
        previousState = currentState;
        return region;
    }
    public void killed(){
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = MrMario.NOTHING_BIT;
        for (Fixture fixture: b2body.getFixtureList())
            fixture.setFilterData(filter);
        b2body.applyLinearImpulse(new Vector2(0,6f), b2body.getWorldCenter(), true);
    }
}
