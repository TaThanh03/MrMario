package com.thanhta.mrmario.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.thanhta.mrmario.MrMario;
import com.thanhta.mrmario.Scenes.Hud;
import com.thanhta.mrmario.Screens.PlayScreen;
import com.thanhta.mrmario.Sprites.Mario;

public class Brick extends com.thanhta.mrmario.Sprites.TileObjects.InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object) {
        super(screen , object);
        fixture.setUserData(this);
        setCategoryFilter(MrMario.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if (mario.isBig()) {
            Gdx.app.log("hit brick", "");
            setCategoryFilter(MrMario.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(200);
            MrMario.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
        else
            MrMario.manager.get("audio/sounds/bump.wav", Sound.class).play();
    }
}
