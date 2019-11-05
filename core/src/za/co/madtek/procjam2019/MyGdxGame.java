package za.co.madtek.procjam2019;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class MyGdxGame extends ApplicationAdapter {

	private MapGenerator gen;
	private Camera camera;
	private StretchViewport viewport;
	
	@Override
	public void create () {
		camera = new OrthographicCamera();
		viewport = new StretchViewport(256, 144, camera);
		viewport.apply();

		this.gen = new MapGenerator(128, 72, 8);
		gen.generate();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		gen.updateRenderMatrix(camera.combined);

		camera.update();

		gen.render();

		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) gen.generate();
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			camera.translate(1, 0, 0);
		} else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			camera.translate(-1, 0, 0);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			camera.translate(0, 1, 0);
		} else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			camera.translate(0, -1, 0);
		}
	}
	
	@Override
	public void dispose () {
		//--
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}
