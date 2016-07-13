package com.thanhta.mrmario.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.thanhta.mrmario.Screens.PlayScreen;

public abstract class Enemy extends Sprite {
    protected final World world;
    protected final PlayScreen screen;
    public Body b2body;

    public Enemy(PlayScreen screen, float x, float y){
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x,y);
        defineEnemy();
    }

    protected abstract void defineEnemy();
}
