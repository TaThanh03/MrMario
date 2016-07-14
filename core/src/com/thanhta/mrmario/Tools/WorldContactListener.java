package com.thanhta.mrmario.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.thanhta.mrmario.MrMario;
import com.thanhta.mrmario.Sprites.Enemy;
import com.thanhta.mrmario.Sprites.InteractiveTileObject;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        //define collision
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
        //mario's head hit somthing, PRACTICE: refactor it to switch down here
        if (fixA.getUserData() == "head" || fixB.getUserData() == "head"){
            Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
            Fixture object = head == fixA ? fixB : fixA;
            if (object.getUserData() != null && InteractiveTileObject.class.isAssignableFrom(object.getUserData().getClass()))
                ((InteractiveTileObject) object.getUserData()).onHeadHit();
        }
        switch (cDef){
            case MrMario.ENEMY_HEAD_BIT | MrMario.MARIO_BIT:
                if (fixA.getFilterData().categoryBits  == MrMario.ENEMY_HEAD_BIT)
                    ((Enemy)fixA.getUserData()).hitOnHead();
                else if (fixB.getFilterData().categoryBits  == MrMario.ENEMY_HEAD_BIT)
                    ((Enemy)fixB.getUserData()).hitOnHead();
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
