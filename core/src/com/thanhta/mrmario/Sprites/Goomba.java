package com.thanhta.mrmario.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.thanhta.mrmario.MrMario;
import com.thanhta.mrmario.Screens.PlayScreen;

public class Goomba extends Enemy {
    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;

    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for (int i=0; i<2; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i*16, 0, 16 ,16));
        walkAnimation = new Animation(0.4f, frames);
        stateTime = 0;
        setBounds(getX(),getY(), 16/ MrMario.PPM, 16/ MrMario.PPM);
        setToDestroy = false;
        destroyed = false;
    }
    public void update(float dt){
        stateTime += dt;
        if (setToDestroy && !destroyed){
            world.destroyBody(b2body);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32,0, 16,16));
            stateTime = 0;
        }
        else if (!destroyed) {
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion(walkAnimation.getKeyFrame(stateTime, true));
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(),getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        //know what bit is enemy
        fixtureDef.filter.categoryBits = MrMario.ENEMY_BIT;
        //what fixture can enemy collide?
        fixtureDef.filter.maskBits = MrMario.GROUND_BIT | MrMario.BRICK_BIT | MrMario.MARIO_BIT
                | MrMario.COIN_BIT | MrMario.ENEMY_BIT | MrMario.OBJECT_BIT;
        //create enemy's body
        CircleShape shape = new CircleShape();
        shape.setRadius(7/ MrMario.PPM);
        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);
        //create goomba's head
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

    public void draw(Batch batch){
        //goomba only appear if he is NOT destroyed or he IS destroyed and stateTimer is less than 1
        if (!destroyed || stateTime<1)
            super.draw(batch);
    }

    @Override
    public void hitOnHead() {
        setToDestroy = true;
    }
}
