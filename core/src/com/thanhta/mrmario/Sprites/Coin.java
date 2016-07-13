package com.thanhta.mrmario.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.thanhta.mrmario.MrMario;
import com.thanhta.mrmario.Scenes.Hud;
import com.thanhta.mrmario.Screens.PlayScreen;

public class Coin extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;
    public Coin(PlayScreen screen, Rectangle bounds) {
        super(screen , bounds);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MrMario.COIN_BIT);
    }

    @Override
    public void onHeadHit() {
        Gdx.app.log("hit coin", "");
        if (getCell().getTile().getId() == BLANK_COIN)
            MrMario.manager.get("audio/sounds/bump.wav", Sound.class).play();
        else
            MrMario.manager.get("audio/sounds/coin.wav", Sound.class).play();
        getCell().setTile(tileSet.getTile(BLANK_COIN));
        Hud.addScore(100);

    }
}
