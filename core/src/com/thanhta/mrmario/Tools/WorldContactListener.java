package com.thanhta.mrmario.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.thanhta.mrmario.MrMario;
import com.thanhta.mrmario.Sprites.Enemies.Enemy;
import com.thanhta.mrmario.Sprites.Items.Item;
import com.thanhta.mrmario.Sprites.Mario;
import com.thanhta.mrmario.Sprites.TileObjects.InteractiveTileObject;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        //define collision
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
        switch (cDef){
            case MrMario.MARIO_HEAD_BIT | MrMario.BRICK_BIT:
            case MrMario.MARIO_HEAD_BIT | MrMario.COIN_BIT:
                if (fixA.getFilterData().categoryBits  == MrMario.MARIO_HEAD_BIT)
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Mario)fixA.getUserData());
                else
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
                break;
            case MrMario.ENEMY_HEAD_BIT | MrMario.MARIO_BIT:
                if (fixA.getFilterData().categoryBits  == MrMario.ENEMY_HEAD_BIT)
                    ((Enemy)fixA.getUserData()).hitOnHead();
                else
                    ((Enemy)fixB.getUserData()).hitOnHead();
                break;
            case MrMario.ENEMY_BIT | MrMario.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits  == MrMario.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case MrMario.MARIO_BIT | MrMario.ENEMY_BIT:
                Gdx.app.log("MARIO", "died");
                break;
            case MrMario.ENEMY_BIT | MrMario.ENEMY_BIT:
                ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case MrMario.ITEM_BIT | MrMario.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits  == MrMario.ITEM_BIT)
                    ((Item)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case MrMario.ITEM_BIT | MrMario.MARIO_BIT:
                if (fixA.getFilterData().categoryBits  == MrMario.ITEM_BIT)
                    ((Item)fixA.getUserData()).use((Mario) fixB.getUserData());
                else
                    ((Item)fixB.getUserData()).use((Mario) fixA.getUserData());
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
//        Gdx.app.log("end contact","");
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
