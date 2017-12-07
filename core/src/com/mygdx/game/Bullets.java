package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


/**
 * Created by Raniel on 5/16/2017.
 */

public class Bullets extends Thread{

    public static final float SPEED = 100.0f;
    private static Texture texture;
    private Sprite sprite;
    private int rotation;

    private float centerX = 0, centerY = 0;
    CollisionRect rect;

    public boolean remove = false;

    public Bullets(float centerX, float centerY, int rotation){

        this.centerX = centerX;
        this.centerY = centerY;
        this.rotation = rotation;
        if(texture==null) {
            texture = new Texture(Gdx.files.internal("bullet.png"));
        }

        sprite = new Sprite(texture);
        sprite.setCenter(centerX,centerY);
        sprite.setRotation(rotation);
        this.rect = new CollisionRect(sprite.getX(),sprite.getY(),(int)sprite.getWidth(),(int)sprite.getHeight());
    }

    public void update(){
        if(rotation>90) {
            this.centerY -= Gdx.graphics.getDeltaTime() * SPEED ;
            if(rotation>=115) {
                this.centerX -= Gdx.graphics.getDeltaTime() * (SPEED*4);
            }else if(rotation>100) {
                this.centerX -= Gdx.graphics.getDeltaTime() * (SPEED+2) + 10;
            }else {
                this.centerX -= Gdx.graphics.getDeltaTime() * (SPEED-5) + 15;
            }

        }else if(rotation < 90) {
            this.centerY += Gdx.graphics.getDeltaTime() * SPEED;
            if(rotation<=65) {
                this.centerX -= Gdx.graphics.getDeltaTime() * (SPEED*4);
            }else if(rotation<80) {
                this.centerX -= Gdx.graphics.getDeltaTime() * (SPEED+2) + 10;
            }else {
                this.centerX -= Gdx.graphics.getDeltaTime() * (SPEED-5) + 15;
            }
        }else{
            this.centerX -= Gdx.graphics.getDeltaTime() * SPEED *4;
        }
        if(centerX<0 || centerY<0 || centerY>Gdx.graphics.getHeight()){
            remove = true;
            return;
        }
        sprite.setCenter(centerX,centerY);
        rect.move(sprite.getX(),sprite.getY());
    }

    public void render(SpriteBatch batch){
        sprite.draw(batch);
    }

    public Texture getBullet(){
        return texture;
    }

    public CollisionRect getCollisionRect(){
        return rect;
    }
}
