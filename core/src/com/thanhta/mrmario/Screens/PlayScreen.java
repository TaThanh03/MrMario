package com.thanhta.mrmario.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thanhta.mrmario.MrMario;
import com.thanhta.mrmario.Scenes.Hud;

public class PlayScreen implements Screen {
    private MrMario game;
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

    public PlayScreen(MrMario game){
        this.game = game;
        //create cam to follow mario through cam world
        gamecam = new OrthographicCamera();
        //create fit view port to maintain virtual aspect ratio despite screens
        gamePort = new FitViewport(MrMario.V_WIDTH, MrMario.V_HEIGHT,gamecam);
        //create hud for game info
        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        //reposition to right mario position
        gamecam.position.set(gamePort.getWorldWidth()/2, gamePort.getWorldHeight()/2, 0);

        world = new World(new Vector2(0,0), true);
        b2dr = new Box2DDebugRenderer();
     
    }
    @Override
    public void show() {

    }
    private void handleInput(float dt) {
        if (Gdx.input.isTouched()){
            gamecam.position.x += 100*dt;
        }
    }
    public void update(float dt) {
        handleInput(dt);
        gamecam.update();
        renderer.setView(gamecam);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();
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
    public void dispose() {}
}
