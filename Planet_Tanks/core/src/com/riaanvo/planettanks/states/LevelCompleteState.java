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
 * This class creates the level complete screen shown when the player beats the level. It extends
 * from the state super class. It allows the player to play the next level if there is one or quit to the main menu.
 */

public class LevelCompleteState extends State {

    private LevelManager mLevelManager;

    private Stage mStage;
    private Skin mSkin;

    private Label mTitle;
    private TextButton mNextLevelButton;
    private TextButton mMainMenuButton;

    private State mPlayState;

    //Used to control the transition between states
    private boolean mTransitionedIn;
    private boolean mTransitionOut;
    private float mFadeInTime;
    private float mFadeInTimer;
    private float mAlpha;
    private Texture mBlackFadeTexture;

    public LevelCompleteState() {
        mPlayState = mGameStateManager.getState(0);
        mLevelManager = LevelManager.get();
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
            //Check if the state has transitioned in
            if (mFadeInTimer >= mFadeInTime) {
                mFadeInTimer = mFadeInTime;
                mTransitionedIn = true;
            }

        } else {
            mStage.act(deltaTime);
            if (mTransitionOut) {
                mFadeInTimer -= deltaTime;
                //Check if the state has transitioned out
                if (mFadeInTimer < 0) {
                    mFadeInTimer = 0;
                    mGameStateManager.pop();
                }

            }
        }

        //Update the play state if it is set
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
        //Calculate the alpha
        float currentAlpha = mAlpha * (mFadeInTimer / mFadeInTime);
        if (mBlackFadeTexture == null) return;
        spriteBatch.setColor(0, 0, 0, currentAlpha);
        spriteBatch.begin();
        //Render the black fade texture with the alpha
        spriteBatch.draw(mBlackFadeTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();
        spriteBatch.setColor(0, 0, 0, 1);

        //Draw the UI if not transitioning
        if (mTransitionedIn && !mTransitionOut) mStage.draw();
    }

    @Override
    protected void loaded() {
        mStage = new Stage(new ScalingViewport(Scaling.stretch, Constants.VIRTUAL_SCREEN_WIDTH, Constants.VIRTUAL_SCREEN_HEIGHT));
        mSkin = mContentManager.getSkin(Constants.SKIN_KEY);

        mBlackFadeTexture = mContentManager.getTexture(Constants.BLACK_TEXTURE);

        //Create the tile label
        mTitle = new Label("LEVEL " + mLevelManager.getLevelName() + "\nCOMPLETE!", mSkin, Constants.TITLE_FONT);
        mTitle.setFontScale(2);
        mTitle.setAlignment(Align.center);

        //Create the buttons for the options
        mNextLevelButton = new TextButton("NEXT LEVEL", mSkin);
        mNextLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Load the next level and transition out
                mLevelManager.LoadNextLevel();
                mTransitionOut = true;
            }
        });


        mMainMenuButton = new TextButton("QUIT", mSkin);
        mMainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //remove the play state and this state
                mGameStateManager.removeState(1);
                mGameStateManager.pop();
            }
        });

        //Create the table that will be used to structure the UI
        Table mTable = new Table();
        mTable.setTransform(true);
        mTable.padBottom(20f);
        mTable.setBounds(0, 0, mStage.getWidth(), mStage.getHeight());

        float buttonWidth = 180;
        float buttonHeight = 80;

        mTable.add(mTitle).pad(10f);
        mTable.row();
        //Add the next level button if there is another level to be played
        if (mLevelManager.isAnotherLevel()) {
            mLevelManager.UnlockNextLevel();
            mTable.add(mNextLevelButton).pad(10f).width(buttonWidth).height(buttonHeight);
            mTable.row();
        }
        mTable.add(mMainMenuButton).pad(10f).width(buttonWidth).height(buttonHeight);

        mStage.addActor(mTable);
    }

    @Override
    public void initialiseInput() {
        if (mStage == null) return;
        Gdx.input.setInputProcessor(mStage);
    }


    @Override
    public void dispose() {
        if (mStage == null) return;
        mStage.dispose();
    }
}
