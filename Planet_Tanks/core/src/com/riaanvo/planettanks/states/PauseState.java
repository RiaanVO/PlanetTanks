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

/**
 * This class creates and displays a simple pause menu to the player. It extends functionality from
 * the state superclass. It allows the player to pause the game, resume playing or quit to main menu
 */

public class PauseState extends State {
    private Stage mStage;
    private Skin mSkin;

    private Label mTitle;
    private TextButton mResumeButton;
    private TextButton mMainMenuButton;

    private State mPlayState;

    //Used to control the transitioning
    private boolean mTransitionedIn;
    private boolean mTransitionOut;
    private float mFadeInTime;
    private float mFadeInTimer;
    private float mAlpha;
    private Texture mBlackFadeTexture;

    public PauseState() {
        mPlayState = mGameStateManager.getState(0);
        mAlpha = 0.8f;
        mTransitionedIn = false;
        mTransitionOut = false;
        mFadeInTime = 0.2f;
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
        //Render the black texture with the calculated alpha
        spriteBatch.draw(mBlackFadeTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();
        spriteBatch.setColor(0, 0, 0, 1);

        //If the state is not transitioning render the UI
        if (mTransitionedIn && !mTransitionOut) mStage.draw();
    }

    @Override
    protected void loaded() {
        mStage = new Stage(new ScalingViewport(Scaling.stretch, Constants.VIRTUAL_SCREEN_WIDTH, Constants.VIRTUAL_SCREEN_HEIGHT));
        mSkin = mContentManager.getSkin(Constants.SKIN_KEY);

        mBlackFadeTexture = mContentManager.getTexture(Constants.BLACK_TEXTURE);

        //Create the title label
        mTitle = new Label("PAUSED", mSkin, "title");
        mTitle.setFontScale(2);
        mTitle.setAlignment(Align.center);

        //Create the option buttons
        mResumeButton = new TextButton("RESUME", mSkin);
        mResumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mTransitionOut = true;
            }
        });

        mMainMenuButton = new TextButton("QUIT", mSkin);
        mMainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Remove the play state and this state
                mGameStateManager.removeState(1);
                mGameStateManager.pop();
            }
        });

        //Create the UI structure to hold all the elements
        Table mTable = new Table();
        mTable.setTransform(true);
        mTable.padBottom(20f);
        mTable.setBounds(0, 0, mStage.getWidth(), mStage.getHeight());

        float buttonWidth = 180;
        float buttonHeight = 80;

        mTable.add(mTitle).pad(10f);
        mTable.row();
        mTable.add(mResumeButton).pad(10f).width(buttonWidth).height(buttonHeight);
        mTable.row();
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
