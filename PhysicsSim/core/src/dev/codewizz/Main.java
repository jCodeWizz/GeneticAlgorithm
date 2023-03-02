package dev.codewizz;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;

import dev.codewizz.physics2D.World;
import dev.codewizz.utils.StopWatch;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Main extends ApplicationAdapter {

	public static boolean DEBUG = true;
	public static ShapeDrawer shapeDrawer;
	public static BitmapFont font;
	
	
	private TextureRegion WHITE;
	private SpriteBatch spriteBatch;
	private SpriteBatch uiBatch;
	private OrthographicCamera camera;
	private World world;
	private int iterations = 8;

	private StopWatch physicsWatch;
	private StopWatch totalWatch;
	private long longestPhysics = 0L;
	private long longestTotal = 0L;
	
	
	@Override
	public void create() {
		physicsWatch = new StopWatch();
		totalWatch = new StopWatch();
		world = new World();
		spriteBatch = new SpriteBatch();
		uiBatch = new SpriteBatch();
		WHITE = new TextureRegion(new Texture("white.png"));
		font = new BitmapFont();
		shapeDrawer = new ShapeDrawer(spriteBatch, WHITE);
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		camera = new OrthographicCamera(1000, 1000 * (h / w));
		camera.update();
	}

	@Override
	public void render() {
		totalWatch.start();
		ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1f);
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);

		physicsWatch.start();
		world.update(Gdx.graphics.getDeltaTime(), iterations);
		physicsWatch.end();

		if (longestPhysics < physicsWatch.getTime())
			longestPhysics = physicsWatch.getTime();

		spriteBatch.begin();
		world.render(spriteBatch);
		spriteBatch.end();

		
		totalWatch.end();
		
		if(longestTotal < totalWatch.getTime()) {
			longestTotal = totalWatch.getTime();
		}
		
		uiBatch.begin();
		
		/*
		font.draw(uiBatch, "Longest Physics Step: " + longestPhysics + " ms", 20, Gdx.graphics.getHeight() - 20);
		font.draw(uiBatch, "Current Physics Step: " + physicsWatch.getTime() + " ms", 20, Gdx.graphics.getHeight() - 40);
		font.draw(uiBatch, "Longest Total Step: " + longestTotal + " ms", 20, Gdx.graphics.getHeight() - 70);
		font.draw(uiBatch, "Current Total Step: " + totalWatch.getTime() + " ms", 20, Gdx.graphics.getHeight() - 90);
		font.draw(uiBatch, "Amount of Objects: " + world.objects.size(), 20, Gdx.graphics.getHeight()-120);
		font.draw(uiBatch, "Amount of Collisions: " + world.results.size(), 20, Gdx.graphics.getHeight()-140);
		*/
		
		
		uiBatch.end();
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
	}
}
