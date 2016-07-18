package com.thanhta.mrmario.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thanhta.mrmario.MrMario;
import com.thanhta.mrmario.Scenes.Hud;
import com.thanhta.mrmario.Sprites.Enemies.Enemy;
import com.thanhta.mrmario.Sprites.Items.Item;
import com.thanhta.mrmario.Sprites.Items.ItemDef;
import com.thanhta.mrmario.Sprites.Items.Mushroom;
import com.thanhta.mrmario.Sprites.Mario;
import com.thanhta.mrmario.Tools.B2WorldCreator;
import com.thanhta.mrmario.Tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingDeque;

public class PlayScreen implements Screen {
    //reference to game, used to set screen
    private MrMario game;

    private TextureAtlas atlas;
    //basic play screen variables
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;
    //tiled map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    //box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;
    //sprites
    private Mario player;
    //items
    private Array<Item> items;
    private LinkedBlockingDeque<ItemDef> itemsToSpawn;
    //music, sound
    private Music music;



    public PlayScreen(MrMario game){
        atlas = new TextureAtlas("Mario_and_Enemies.pack");
        this.game = game;
        //create cam to follow mario through cam world
        gamecam = new OrthographicCamera();
        //create fit view port to maintain virtual aspect ratio despite screens
        //scale with PPM
        gamePort = new FitViewport(MrMario.V_WIDTH / MrMario.PPM, MrMario.V_HEIGHT/ MrMario.PPM,gamecam);
        //create hud for game info
        hud = new Hud(game.batch);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        //scale with PPM
        renderer = new OrthogonalTiledMapRenderer(map, 1/ MrMario.PPM);
        //reposition to right mario position
        gamecam.position.set(gamePort.getWorldWidth()/2, gamePort.getWorldHeight()/2, 0);
        //Scale with PPM (-10/PPM)
        world = new World(new Vector2(0,-10), true);
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);

        //create mario in game world
        player = new Mario(this);

        world.setContactListener(new WorldContactListener());
//        music = MrMario.manager.get("audio/music/mario_music.ogg", Music.class);
//        music.setLooping(true);
//        music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingDeque<ItemDef>();
    }
    public void spawnItem(ItemDef itemDef){
        itemsToSpawn.add(itemDef);
    }

    public void handleSpawningItems(){
        if (!itemsToSpawn.isEmpty()){
            ItemDef itemDef = itemsToSpawn.poll();
            if (itemDef.type == Mushroom.class){
                items.add(new Mushroom(this, itemDef.position.x, itemDef.position.y));
            }
        }
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }
    @Override
    public void show() {}
    private void handleInput(float dt) {
        if (player.currentState != Mario.State.DEAD){
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
                player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        }
    }
    public void update(float dt) {
        //handle user input first
        handleInput(dt);
        handleSpawningItems();
        //set up physical attribute
        world.step(1/60f, 6,2);
        //update player
        player.update(dt);
        for (Enemy enemy: creator.getEnemies()) {
            enemy.update(dt);
            //active when mario get close
            if(enemy.getX() < player.getX() +  200/MrMario.PPM)
                enemy.b2body.setActive(true);
        }
        for (Item item : items)
            item.update(dt);
        hud.update(dt);
        if (player.currentState != Mario.State.DEAD)
            gamecam.position.x = player.b2body.getPosition().x;
        //update our gamecam with correct coordinate after changes
        gamecam.update();
        //renderer draws only what our camera can see in game world
        renderer.setView(gamecam);
    }

    @Override
    public void render(float delta) {
        //separate our update logic from render
        update(delta);
        //clear the game screen with black
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //render game map
        renderer.render();
        //render Box2ddebugLines
        b2dr.render(world,gamecam.combined);
        //render mario
        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for (Enemy enemy: creator.getEnemies())
            enemy.draw(game.batch);
        for (Item item : items)
            item.draw(game.batch);
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        if (gameOver()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }
    public boolean gameOver(){
        if (player.currentState == Mario.State.DEAD && player.getStateTimer() > 2)
            return true;
        return false;
    }
    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

    public TiledMap getMap(){
        return map;
    }

    public World getWorld(){
        return world;
    }
}
