package com.thanhta.mrmario.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.thanhta.mrmario.MrMario;
import com.thanhta.mrmario.Scenes.Hud;
import com.thanhta.mrmario.Screens.PlayScreen;
import com.thanhta.mrmario.Sprites.Items.ItemDef;
import com.thanhta.mrmario.Sprites.Items.Mushroom;
import com.thanhta.mrmario.Sprites.Mario;

public class Coin extends com.thanhta.mrmario.Sprites.TileObjects.InteractiveTileObject {
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;
    public Coin(PlayScreen screen, MapObject object) {
        super(screen , object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MrMario.COIN_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        Gdx.app.log("hit coin", "");
        if (getCell().getTile().getId() == BLANK_COIN)
            MrMario.manager.get("audio/sounds/bump.wav", Sound.class).play();
        else {
            if (object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MrMario.PPM), Mushroom.class));
                MrMario.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            } else
                MrMario.manager.get("audio/sounds/coin.wav", Sound.class).play();
        }
        getCell().setTile(tileSet.getTile(BLANK_COIN));
        Hud.addScore(100);

    }
}
