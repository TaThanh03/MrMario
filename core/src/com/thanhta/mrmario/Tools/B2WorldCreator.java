package com.thanhta.mrmario.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.thanhta.mrmario.MrMario;
import com.thanhta.mrmario.Screens.PlayScreen;
import com.thanhta.mrmario.Sprites.TileObjects.Brick;
import com.thanhta.mrmario.Sprites.TileObjects.Coin;
import com.thanhta.mrmario.Sprites.Enemies.Goomba;

public class B2WorldCreator {

    private Array<Goomba> goombas;

    public B2WorldCreator(PlayScreen screen){
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;
        //create ground bodies/fixtures
        for (MapObject object: map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth()/2)/ MrMario.PPM, (rect.getY() + rect.getHeight()/2)/ MrMario.PPM);
            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth()/2/ MrMario.PPM, rect.getHeight()/2/ MrMario.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MrMario.GROUND_BIT;
            body.createFixture(fdef);
        }
        //create pipe bodies/fixtures
        for (MapObject object: map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth()/2)/ MrMario.PPM, (rect.getY() + rect.getHeight()/2)/ MrMario.PPM);
            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth()/2/ MrMario.PPM, rect.getHeight()/2/ MrMario.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MrMario.OBJECT_BIT;
            body.createFixture(fdef);
        }
        //create brick bodies/fixtures
        for (MapObject object: map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            new Brick(screen, object);
        }
        //create coin bodies/fixtures
        for (MapObject object: map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            new Coin(screen, object);
        }
        //create goombas
        goombas = new Array<Goomba>();
        for (MapObject object: map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            goombas.add(new Goomba(screen, rect.getX()/ MrMario.PPM, rect.getY()/ MrMario.PPM));
        }
    }

    public Array<Goomba> getGoombas() {
        return goombas;
    }
}
