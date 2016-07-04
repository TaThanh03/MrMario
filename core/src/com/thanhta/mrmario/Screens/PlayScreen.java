package com.thanhta.mrmario.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thanhta.mrmario.MrMario;
import com.thanhta.mrmario.Scenes.Hud;
import com.thanhta.mrmario.Sprites.Mario;
import com.thanhta.mrmario.Tools.B2WorldCreator;

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

    private Mario player;



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

        new B2WorldCreator(world,map);
        //create mario in game world
        player = new Mario(world, this);
    }
    public TextureAtlas getAtlas(){
        return atlas;
    }
    @Override
    public void show() {}
    private void handleInput(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
            player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2)
            player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2)
            player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
    }
    public void update(float dt) {
        //handle user input first
        handleInput(dt);
        //set up physical attribute
        world.step(1/60f, 6,2);
        //update player
        player.update(dt);

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
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
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
}
