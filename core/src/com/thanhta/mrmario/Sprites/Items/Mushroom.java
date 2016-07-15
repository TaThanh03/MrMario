package com.thanhta.mrmario.Sprites.Items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.thanhta.mrmario.MrMario;
import com.thanhta.mrmario.Screens.PlayScreen;
import com.thanhta.mrmario.Sprites.Mario;

public class Mushroom extends Item {
    public Mushroom(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("mushroom"), 0 ,0, 16,16);
        velocity = new Vector2(0.7f,0);
    }

    @Override
    protected void defineItem() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(),getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        //create mushroom's body
        CircleShape shape = new CircleShape();
        shape.setRadius(7/ MrMario.PPM);
        fixtureDef.filter.categoryBits = MrMario.ITEM_BIT;
        fixtureDef.filter.maskBits = MrMario.OBJECT_BIT | MrMario.GROUND_BIT
                | MrMario.COIN_BIT | MrMario.BRICK_BIT | MrMario.MARIO_BIT;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

    }
    @Override
    public void use(Mario mario) {
        destroy();
        mario.grow();
    }
    @Override
    public void update(float dt) {
        super.update(dt);
        setPosition(body.getPosition().x - getWidth()/2, body.getPosition().y - getHeight()/2);
        velocity.y = body.getLinearVelocity().y;
        body.setLinearVelocity(velocity);
    }
}
