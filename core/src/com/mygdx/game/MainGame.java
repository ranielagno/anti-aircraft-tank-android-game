package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.StreamUtils;

import org.w3c.dom.Text;

import java.awt.Color;
import java.awt.geom.Arc2D;
import java.util.ArrayList;

import jdk.nashorn.internal.objects.Global;

public class MainGame extends ApplicationAdapter implements InputProcessor{

	SpriteBatch batch;
	Texture backgroundImage, tankImage, cannonImage;
	private Sprite spriteBackground, spriteTank, spriteCannon, spriteScore;
	private BitmapFont bitmapScore, font;

	private float screenWidth, screenHeight, timePassed;
	private float bulletTime = 0 ;
	private double slope = 0;
	private int cannonDegrees = 90;
	int score = 0;

	ArrayList<Bullets> bullet;
	ArrayList<Bullets> bulletsToRemove;
	Ufo ufo;
	Animation explosion;
	Texture textureExplode;

	@Override
	public void create () {

		bullet = new ArrayList<Bullets>();
		bulletsToRemove = new ArrayList<Bullets>();

		batch = new SpriteBatch();
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();

		bitmapScore = new BitmapFont(Gdx.files.internal("fonts/score.fnt"));
		GlyphLayout scoreLayout = new GlyphLayout(bitmapScore, "Score: "+score);
		//bitmapScore.draw(batch, scoreLayout, Gdx.graphics.getWidth()/2 - scoreLayout.width / 2, Gdx.graphics.getHeight() - scoreLayout.height);
		spriteScore = new Sprite(bitmapScore.getRegion().getTexture());
		spriteScore.setRotation(90);
		/*
		//
		CharSequence charSequence = score;
		scoreLayout = new GlyphLayout(bitmapScore,charSequence);
		*/

		backgroundImage = new Texture(Gdx.files.internal("background.png"));
		spriteBackground = new Sprite(backgroundImage, (int)screenHeight, (int)screenWidth);
		spriteBackground.setCenter(screenWidth/2, screenHeight/2);
		spriteBackground.setRotation(90);

		tankImage = new Texture(Gdx.files.internal("tank.png"));
		spriteTank = new Sprite(tankImage);
		spriteTank.setCenter(screenWidth-50, screenHeight/2);
		spriteTank.setRotation(90);

		cannonImage = new Texture(Gdx.files.internal("cannon.png"));
		spriteCannon = new Sprite(cannonImage);
		spriteCannon.setCenter(screenWidth-50, screenHeight/2);
		spriteCannon.setRotation(90);
		ufo = new Ufo();

		textureExplode = new Texture(Gdx.files.internal("explosion.png"));
		TextureAtlas e = new TextureAtlas(Gdx.files.internal("explosion.atlas"));
		explosion = new Animation <TextureRegion>(1/3f, e.getRegions());
		Gdx.input.setInputProcessor(this);

	}

	@Override
	public void render () {

		Gdx.gl.glClearColor(255, 255, 255, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		bulletTime += Gdx.graphics.getDeltaTime();

		batch.begin();

		if(bulletTime>2){
			float x = (spriteCannon.getVertices()[SpriteBatch.X2] + spriteCannon.getVertices()[SpriteBatch.X3])/2;
			float y = (spriteCannon.getVertices()[SpriteBatch.Y2] + spriteCannon.getVertices()[SpriteBatch.Y3])/2;
			bullet.add(new Bullets(x, y,cannonDegrees));
			bulletTime = 0;
		}

		if(Ufo.destroyed) {
			ufo = new Ufo();
			Ufo.destroyed = false;
		}
		spriteBackground.draw(batch);
		spriteTank.draw(batch);
		spriteCannon.draw(batch);
		ufo.render(batch);

		for(Bullets b : bullet){
			b.update();
			b.render(batch);

			if (b.remove) {
				bulletsToRemove.add(b);
			}

		}

		timePassed += Gdx.graphics.getDeltaTime();

		for(Bullets b : bullet){

			if (b.getCollisionRect().collidesWith(ufo.getCollisionRect())) {
				bulletsToRemove.add(b);
				batch.draw((TextureRegion) explosion.getKeyFrame(timePassed,true),ufo.getX(),ufo.getY());

				if(explosion.isAnimationFinished(timePassed)){
					Ufo.destroyed = true;
					score += 100;
				}

			}

		}

		bullet.removeAll(bulletsToRemove);
		bulletsToRemove.clear();

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		backgroundImage.dispose();
		tankImage.dispose();
		bitmapScore.dispose();
		cannonImage.dispose();
		bullet.get(0).getBullet().dispose();
		bullet.clear();
		ufo.getUFO().dispose();
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		double adjacent = 0, opposite = 0, angle = 0;

		if(screenY > screenHeight/2){
			adjacent = screenY - (screenHeight/2);
			opposite = (screenWidth-50) - screenX ;
			angle = 180 - (Math.toDegrees(Math.atan2(opposite,adjacent))+90);
			if(angle<40) {
				spriteCannon.setRotation(90 + (int) angle);
				cannonDegrees = 90 + (int) angle;

				if((int)angle == 0){
					slope = 0;
				}else {
					slope = Math.tan(cannonDegrees);
				}
			}
		}else{
			adjacent = (screenHeight/2) - screenY;
			opposite = (screenWidth-50) - screenX ;
			angle = 180 - (Math.toDegrees(Math.atan2(opposite,adjacent))+90);
			if(angle<43) {
				spriteCannon.setRotation(90 - (int) angle);
				cannonDegrees = 90 - (int) angle;

				if((int)angle == 0){
					slope = 0;
				}else {
					slope = Math.tan(cannonDegrees);
				}
			}
		}

		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
