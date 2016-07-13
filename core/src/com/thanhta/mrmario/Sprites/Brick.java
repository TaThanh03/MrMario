package com.thanhta.mrmario.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.thanhta.mrmario.MrMario;
import com.thanhta.mrmario.Scenes.Hud;
import com.thanhta.mrmario.Screens.PlayScreen;

public class Brick extends  InteractiveTileObject {
    public Brick(PlayScreen screen, Rectangle bounds) {
        super(screen , bounds);
        fixture.setUserData(this);
        setCategoryFilter(MrMario.BRICK_BIT);
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("hit brick", "");
        setCategoryFilter(MrMario.DESTROYED_BIT);
        getCell().setTile(null);
        Hud.addScore(200);
        MrMario.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
    }
}
