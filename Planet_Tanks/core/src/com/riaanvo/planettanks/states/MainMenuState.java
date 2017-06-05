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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.managers.AdManager;

/**
 * This class creates and displays the games main menu. It extends functionality from the state
 * super class. It allows players to move to the level select screen, editor menu screen or to
 * quit the application. It also displays an advert to the player
 */

public class MainMenuState extends State {

    private Stage mStage;
    private Skin mSkin;

    private Label mTitle;
    private TextButton mPlayButton;
    private TextButton mLevelEditorButton;
    private TextButton mQuitButton;
    private Image mBackgroundImage;

    public MainMenuState() {
    }

    @Override
    protected void update(float deltaTime) {
    }

    @Override
    protected void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        mStage.draw();
    }

    @Override
    protected void loaded() {
        mStage = new Stage(new ScalingViewport(Scaling.stretch, Constants.VIRTUAL_SCREEN_WIDTH, Constants.VIRTUAL_SCREEN_HEIGHT));
        mSkin = mContentManager.getSkin(Constants.SKIN_KEY);

        //load and set up the background image
        mBackgroundImage = new Image(mContentManager.getTexture(Constants.MAIN_MENU_BACKGROUND));
        mBackgroundImage.setPosition(0, 0);
        mBackgroundImage.setSize(mStage.getWidth(), mStage.getHeight());
        mStage.addActor(mBackgroundImage);

        //Create the title label
        mTitle = new Label("PLANET TANKS", mSkin, "title");
        mTitle.setFontScale(2);
        mTitle.setAlignment(Align.center);

        //Create the menu buttons
        mPlayButton = new TextButton("PLAY", mSkin);
        mPlayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Attempt to show and advert
                AdManager.get().showInterstitialAd();
                //Display the level select state
                mGameStateManager.push(new LevelSelectState());
            }
        });

        mLevelEditorButton = new TextButton("LEVEL EDITOR", mSkin);
        mLevelEditorButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Display the Editor menu state
                mGameStateManager.push(new EditorMenuState());
            }
        });

        mQuitButton = new TextButton("QUIT GAME", mSkin);
        mQuitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Close the application
                Gdx.app.exit();
            }
        });


        //Set up the table that will hold the UI elements
        Table mTable = new Table();
        mTable.setTransform(true);
        mTable.padBottom(20f);
        mTable.setBounds(0, 0, mStage.getWidth(), mStage.getHeight());

        float buttonWidth = 180;
        float buttonHeight = 80;

        //Add the UI elements to the table
        mTable.add(mTitle).pad(10f);
        mTable.row();
        mTable.add(mPlayButton).pad(10f).width(buttonWidth).height(buttonHeight);
        mTable.row();
        mTable.add(mLevelEditorButton).pad(10f).width(buttonWidth).height(buttonHeight);
        mTable.row();
        mTable.add(mQuitButton).pad(10f).width(buttonWidth).height(buttonHeight);

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
