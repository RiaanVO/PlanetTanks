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

package com.riaanvo.planettanks.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.PlanetTanks;

/**
 * This class creates a splash screen shown at the start of running the application. It extends functionality from
 * the state superclass. It forces the app to load all the games assets and continues on to the menu
 * state once all assets have been loaded.
 */

public class SplashScreenState extends State {
    private Stage mStage;

    private Image mBackgroundImage;

    //Used to control when to move to the next screen
    private float mDuration = 1;
    private boolean mAllAssetsLoaded;
    private boolean hasTransitioned;

    private PlanetTanks mPlanetTanks;

    public SplashScreenState(PlanetTanks planetTanks) {
        mStage = new Stage(new ScalingViewport(Scaling.stretch, Constants.VIRTUAL_SCREEN_WIDTH, Constants.VIRTUAL_SCREEN_HEIGHT));

        //Splash screen assets
        mContentManager.loadTexture(Constants.SPLASH_BACKGROUND);

        //Load required textures and fonts
        hasTransitioned = false;
        mAllAssetsLoaded = false;
        mPlanetTanks = planetTanks;
    }

    @Override
    protected void update(float deltaTime) {
        if (mAllAssetsLoaded) {
            mDuration -= deltaTime;
            //Check if the screen change delay has ended
            if (mDuration < 0 && !hasTransitioned) {
                hasTransitioned = true;
                //Load the main menu screen
                mGameStateManager.setState(new MainMenuState());
            }
        } else {
            //Check if all assets have been loaded
            mAllAssetsLoaded = mContentManager.assetManagerUpdate();
        }
    }

    @Override
    protected void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        mStage.draw();
    }

    @Override
    protected void loaded() {
        //Set up the background image and display it
        mBackgroundImage = new Image(mContentManager.getTexture(Constants.SPLASH_BACKGROUND));
        mBackgroundImage.setPosition(0, 0);
        mBackgroundImage.setSize(mStage.getWidth(), mStage.getHeight());
        mStage.addActor(mBackgroundImage);

        //Force the game to load all the other assets
        mPlanetTanks.LoadGameAssets();
        mAllAssetsLoaded = false;
    }

    @Override
    public void initialiseInput() {
    }

    @Override
    public void dispose() {
        mStage.dispose();
    }
}
