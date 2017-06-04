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
 * Created by riaanvo on 9/5/17.
 */

public class SplashScreenState extends State {
    private Stage mStage;
    private boolean hasTransitioned;

    private Image backgroundImage;

    private float duration = 1;
    private boolean allAssetsLoaded;

    private PlanetTanks mPlanetTanks;

    public SplashScreenState(PlanetTanks planetTanks) {
        mStage = new Stage(new ScalingViewport(Scaling.stretch, Constants.VIRTUAL_SCREEN_WIDTH, Constants.VIRTUAL_SCREEN_HEIGHT));

        //Splash screen assets
        mContentManager.loadTexture(Constants.SPLASH_BACKGROUND);

        //Load required textures and fonts
        hasTransitioned = false;
        allAssetsLoaded = false;
        mPlanetTanks = planetTanks;
    }

    @Override
    protected void update(float deltaTime) {
        if (allAssetsLoaded) {
            duration -= deltaTime;
            if (duration < 0 && !hasTransitioned) {
                hasTransitioned = true;
                mGameStateManager.setState(new MainMenuState());
            }
        } else {
            allAssetsLoaded = mContentManager.assetManagerUpdate();
        }
    }

    @Override
    protected void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        mStage.draw();
    }

    @Override
    protected void loaded() {
        backgroundImage = new Image(mContentManager.getTexture(Constants.SPLASH_BACKGROUND));
        backgroundImage.setPosition(0, 0);
        backgroundImage.setSize(mStage.getWidth(), mStage.getHeight());
        mStage.addActor(backgroundImage);

        mPlanetTanks.LoadGameAssets();

        allAssetsLoaded = false;
    }

    @Override
    public void initialiseInput() {
    }

    @Override
    public void dispose() {
        mStage.dispose();
    }
}
