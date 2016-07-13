package com.thanhta.mrmario.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.thanhta.mrmario.MrMario;
import com.thanhta.mrmario.Screens.PlayScreen;

public class Goomba extends Enemy {
    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for (int i=0; i<2; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i*16, 0, 16 ,16));
        walkAnimation = new Animation(0.4f, frames);
        stateTime = 0;
        setBounds(getX(),getY(), 16/ MrMario.PPM, 16/ MrMario.PPM);
    }
    public void update(float dt){
        stateTime += dt;
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight()/2);
        setRegion(walkAnimation.getKeyFrame(stateTime, true));
    }

    @Override
    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(32/ MrMario.PPM,32/ MrMario.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        //know what bit is mario
        fixtureDef.filter.categoryBits = MrMario.ENEMY_BIT;
        //what fixture can mario collide?
        fixtureDef.filter.maskBits = MrMario.GROUND_BIT | MrMario.BRICK_BIT | MrMario.MARIO_BIT
                | MrMario.COIN_BIT | MrMario.ENEMY_BIT | MrMario.OBJECT_BIT;


        //create mario's body
        CircleShape shape = new CircleShape();
        shape.setRadius(7/ MrMario.PPM);
        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef);
    }
}
