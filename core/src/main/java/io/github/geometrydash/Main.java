package io.github.geometrydash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.g2d.BitmapFont;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
//import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.Console;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends ApplicationAdapter {
    ShapeRenderer renderer;
    OrthographicCamera camera;

    ScreenProperties properties = new ScreenProperties();

//    FreeTypeFontGenerator generator;
//    FreeTypeFontParameter parameter;
//    BitmapFont font;

    MousePosition prevMPos;
    MousePosition mPos;

    Stage level;
    Player player;

    int updateRate = 50;
    float fps = 60;
    long prevFrame = 0;

    @Override
    public void create() {
        camera = new OrthographicCamera();

        properties.updateSize(Stage.ratio);

        camera.setToOrtho(false, properties.screenWidth, properties.screenHeight);

//        generator = new FreeTypeFontGenerator(Gdx.files.internal("Arial.ttf"));
//        parameter = new FreeTypeFontParameter();

        updateFont();

        renderer = new ShapeRenderer();

        Gdx.gl.glEnable(GL32.GL_BLEND);
        Gdx.gl.glBlendFunc(GL32.GL_SRC_ALPHA, GL32.GL_ONE_MINUS_SRC_ALPHA);

        level = new Stage();
        player = new Player();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                logic();
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 1000 / updateRate);

    }

    private void updateFont() {
//        parameter.shadowColor = Color.BLACK;
//        parameter.shadowOffsetX = 3;
//        parameter.shadowOffsetY = 3;
//        parameter.size = 18;
//
//        font = generator.generateFont(parameter);
    }

    @Override
    public void render() {
        long time = System.currentTimeMillis();
        fps = 1_000f / (time - prevFrame);
        prevFrame = time;

        properties.updateSize(Stage.ratio);

        renderer.setProjectionMatrix(camera.combined);

        camera.update();

        draw();
    }

    public void reset() {
        player = new Player();
        level = new Stage();
    }

    private void logic() {
        if (Gdx.input.isKeyPressed(Input.Keys.R) || player.reset) {
            reset();
        }
        player.physics(level);
    }

    private void draw() {
        ScreenUtils.clear(0, 0, 0, 0, true);
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        //render

        level.draw(player, renderer, properties);
        player.draw(level, renderer, properties);

        renderer.end();


        //overlay

        prevMPos = mPos;
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
