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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.managers.LevelManager;

/**
 * This class creates the game over screen shown when the player dies. It extends from the state
 * super class. It allows the player to restart the current level or quit to the main menu.
 */

public class GameOverState extends State {
    private Stage mStage;
    private Skin mSkin;

    private Label mTitle;
    private TextButton mReplayButton;
    private TextButton mMainMenuButton;

    private State mPlayState;

    //Used to control the transition
    private boolean mTransitionedIn;
    private boolean mTransitionOut;
    private float mFadeInTime;
    private float mFadeInTimer;
    private Texture mBlackFadeTexture;
    private float mAlpha;

    public GameOverState() {
        mPlayState = mGameStateManager.getState(0);
        mAlpha = 0.8f;
        mTransitionedIn = false;
        mTransitionOut = false;
        mFadeInTime = 0.5f;
        mFadeInTimer = 0f;
    }

    @Override
    protected void update(float deltaTime) {
        if (!mTransitionedIn) {
            mFadeInTimer += deltaTime;
            //Check if the timer has expired
            if (mFadeInTimer >= mFadeInTime) {
                mFadeInTimer = mFadeInTime;
                mTransitionedIn = true;
            }
        } else {
            //Update the stage
            mStage.act(deltaTime);

            if (mTransitionOut) {
                mFadeInTimer -= deltaTime;
                //Check if it has transitioned
                if (mFadeInTimer < 0) {
                    mFadeInTimer = 0;
                    mGameStateManager.pop();
                }
            }
        }

        //Update the play state as fading out
        if (mPlayState != null) {
            mPlayState.Update(deltaTime);
        }
    }

    @Override
    protected void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        //Render the play state if it is set
        if (mPlayState != null) {
            mPlayState.render(spriteBatch, modelBatch);
        }
        //calculate the current black fade alpha
        float currentAlpha = mAlpha * (mFadeInTimer / mFadeInTime);
        if (mBlackFadeTexture == null) return;
        spriteBatch.setColor(0, 0, 0, currentAlpha);
        spriteBatch.begin();
        //Render the black fade
        spriteBatch.draw(mBlackFadeTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();
        spriteBatch.setColor(0, 0, 0, 1);

        //if not transitioning draw the UI menu
        if (mTransitionedIn && !mTransitionOut) mStage.draw();
    }

    @Override
    protected void loaded() {
        //Set up the stage for the UI
        mStage = new Stage(new ScalingViewport(Scaling.stretch, Constants.VIRTUAL_SCREEN_WIDTH, Constants.VIRTUAL_SCREEN_HEIGHT));
        mSkin = mContentManager.getSkin(Constants.SKIN_KEY);

        mBlackFadeTexture = mContentManager.getTexture(Constants.BLACK_TEXTURE);

        //Create the title label
        mTitle = new Label("GAME OVER!", mSkin, "title");
        mTitle.setFontScale(2);
        mTitle.setAlignment(Align.center);

        //Create the menu buttons
        mReplayButton = new TextButton("RETRY", mSkin);
        mReplayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //restart the level and fade out
                LevelManager.get().RestartLevel();
                mTransitionOut = true;
            }
        });

        mMainMenuButton = new TextButton("QUIT", mSkin);
        mMainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // remove the play state and this state
                mGameStateManager.removeState(1);
                mGameStateManager.pop();
            }
        });

        //Set up the UI structure
        Table mTable = new Table();
        mTable.setTransform(true);
        mTable.padBottom(20f);
        mTable.setBounds(0, 0, mStage.getWidth(), mStage.getHeight());

        float buttonWidth = 180;
        float buttonHeight = 80;

        mTable.add(mTitle).pad(10f);
        mTable.row();
        mTable.add(mReplayButton).pad(10f).width(buttonWidth).height(buttonHeight);
        mTable.row();
        mTable.add(mMainMenuButton).pad(10f).width(buttonWidth).height(buttonHeight);

        mStage.addActor(mTable);
    }

    @Override
    public void initialiseInput() {
        if (mStage == null) return;
        //Re assign the input processor
        Gdx.input.setInputProcessor(mStage);
    }

    @Override
    public void dispose() {
        if (mStage == null) return;
        //Dispose of the UI elements
        mStage.dispose();
    }
}
