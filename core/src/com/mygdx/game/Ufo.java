package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;

import java.util.Random;

/**
 * Created by Raniel on 5/17/2017.
 */

public class Ufo {

    private static Texture texture;
    private Sprite sprite;
    private int rotation;

    public static boolean destroyed = false;
    CollisionRect rect;

    public Ufo() {

        if (texture == null) {
            texture = new Texture(Gdx.files.internal("ufo2.png"));
        }

        Random random = new Random();
        /*int startX = random.nextFloat() * Gdx.graphics.getHeight();
        int startY = random.nextFloat() * Gdx.graphics.getHeight();*/
        int endX = random.nextInt(Gdx.graphics.getHeight() - (texture.getHeight() / 2)) + texture.getHeight() / 2;
        int endY = random.nextInt(Gdx.graphics.getWidth() / 2 - (texture.getWidth() / 2)) + texture.getWidth() / 2;

        sprite = new Sprite(texture);
        sprite.setCenter(endX, endY);
        sprite.setRotation(90);
        this.rect = new CollisionRect(sprite.getX(),sprite.getY(),(int)sprite.getWidth(),(int)sprite.getHeight());

    }

    public void render(SpriteBatch batch){
        if(!destroyed)
            sprite.draw(batch);
    }

    public Texture getUFO(){
        return texture;
    }

    public CollisionRect getCollisionRect(){
        return rect;
    }

    public float getX(){
        return sprite.getX();
    }

    public float getY(){
        return sprite.getY();
    }


}
