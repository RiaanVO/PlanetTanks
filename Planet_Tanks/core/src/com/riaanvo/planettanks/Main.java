package com.riaanvo.planettanks;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.riaanvo.planettanks.managers.ContentManager;
import com.riaanvo.planettanks.managers.GameStateManager;
import com.riaanvo.planettanks.states.MainMenuState;
import com.riaanvo.planettanks.states.PlayState;

import java.awt.Font;
import java.sql.Time;

public class Main extends ApplicationAdapter {
    private ModelBatch modelBatch;
    private SpriteBatch spriteBatch;
    private GameStateManager mGameStateManager;
    private ContentManager mContentManager;

    @Override
	public void create () {
        modelBatch = new ModelBatch();
        spriteBatch = new SpriteBatch();
        mGameStateManager = GameStateManager.get();
        mContentManager = ContentManager.get();

        mGameStateManager.push(new MainMenuState());
        //mGameStateManager.push(new PlayState());
    }

	@Override
	public void render () {
        update(Gdx.graphics.getDeltaTime());

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        mGameStateManager.render(spriteBatch, modelBatch);
    }

    public void update(float deltaTime){
        mGameStateManager.update(deltaTime);
    }
	
	@Override
	public void dispose () {
        modelBatch.dispose();
        spriteBatch.dispose();
        mGameStateManager.dispose();
        mContentManager.dispose();
	}
}
