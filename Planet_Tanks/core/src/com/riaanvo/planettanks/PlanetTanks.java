/*
 * Copyright (C) 2017 Riaan Van Onselen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.riaanvo.planettanks;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.riaanvo.planettanks.managers.AdManager;
import com.riaanvo.planettanks.managers.CollisionManager;
import com.riaanvo.planettanks.managers.ContentManager;
import com.riaanvo.planettanks.managers.GameStateManager;
import com.riaanvo.planettanks.managers.LevelManager;
import com.riaanvo.planettanks.states.PlayState;
import com.riaanvo.planettanks.states.SplashScreenState;

/**
 * This is the main game engine class. It initialises the graphics and creates the managers.
 */
public class PlanetTanks extends ApplicationAdapter {
    private ModelBatch mModelBatch;
    private SpriteBatch mSpriteBatch;
    private GameStateManager mGameStateManager;
    private ContentManager mContentManager;
    private AdManager mAdManager;

    private boolean mIsLoaded;
    private boolean mIsDebugging;
    private BitmapFont mDebugFont;

    public PlanetTanks(IActivityRequestHandler handler) {
        mAdManager = AdManager.get();
        mAdManager.setHandler(handler);
        mIsDebugging = false;
    }

    @Override
    public void create() {
        mModelBatch = new ModelBatch();
        mSpriteBatch = new SpriteBatch();
        mGameStateManager = GameStateManager.get();

        mContentManager = ContentManager.get();
        mContentManager.setAssetManager(new AssetManager());

        mIsLoaded = false;

        //Initialise the first screen
        mGameStateManager.push(new SplashScreenState(this));
    }

    @Override
    public void render() {
        //Update the game
        update(Gdx.graphics.getDeltaTime());

        //Clear the screen
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        //Render the game
        mGameStateManager.render(mSpriteBatch, mModelBatch);

        if (!mIsLoaded) {
            if (mContentManager.assetManagerUpdate()) {
                loaded();
            }
            return;
        }

        //Draw the fps if is debugging
        if (mIsDebugging) {
            mSpriteBatch.begin();
            mDebugFont.draw(mSpriteBatch, "fps: " + Gdx.graphics.getFramesPerSecond(), 10, 20);
            mSpriteBatch.end();
        }
    }

    /**
     * Update the game state manager
     *
     * @param deltaTime the time since last update
     */
    public void update(float deltaTime) {
        mGameStateManager.update(deltaTime);
    }

    /**
     * Loads the ui content needed for debugging
     */
    private void loaded() {
        mIsLoaded = true;
        Skin skin = mContentManager.getSkin(Constants.SKIN_KEY);
        mDebugFont = skin.getFont(Constants.DEFAULT_FONT);
    }

    @Override
    public void pause() {
        //Check if the play state was the current state
        PlayState playState;
        try {
            playState = (PlayState) mGameStateManager.getCurrentState();
        } catch (ClassCastException e) {
            System.out.println(e.toString());
            playState = null;
        }

        //Add a pause state if it was
        if (playState != null) {
            playState.pauseGame();
        }
        super.pause();
    }

    @Override
    public void resume() {
        //Reload all the assets
        LoadGameAssets();
        super.resume();
    }

    public void LoadGameAssets() {
        //Load all game assets
        //UI assets
        mContentManager.loadSkin(Constants.SKIN_KEY);
        mContentManager.loadTexture(Constants.BLACK_TEXTURE);

        //Splash screen assets
        mContentManager.loadTexture(Constants.SPLASH_BACKGROUND);

        //Main menu assets
        mContentManager.loadTexture(Constants.MAIN_MENU_BACKGROUND);

        //Game play assets
        mContentManager.loadTexture(Constants.FLOOR_TEXTURE);
        mContentManager.loadModel(Constants.BASIC_TANK_BODY_MODEL);
        mContentManager.loadModel(Constants.BASIC_TANK_TURRET_MODEL);
        mContentManager.loadModel(Constants.SIMPLE_SPIKES_SPIKES);
        mContentManager.loadModel(Constants.SIMPLE_SPIKES_BASE);


        //Editor assets
        mContentManager.loadTexture(Constants.FLOOR_TILE);
        mContentManager.loadTexture(Constants.WALL_TILE);
        mContentManager.loadTexture(Constants.PLAYER_TILE);
        mContentManager.loadTexture(Constants.SPIKES_TILE);
        mContentManager.loadTexture(Constants.ENEMY_TILE);

        mContentManager.CreateBasicModels();
    }

    @Override
    public void dispose() {
        //Force all managers to dispose of their contents
        mModelBatch.dispose();
        mSpriteBatch.dispose();
        mGameStateManager.dispose();
        mContentManager.dispose();
        CollisionManager.get().dispose();
        LevelManager.get().dispose();
        mAdManager.dispose();
    }
}
