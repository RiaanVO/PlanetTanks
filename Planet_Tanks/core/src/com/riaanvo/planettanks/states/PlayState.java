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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.GameObjects.CameraController;
import com.riaanvo.planettanks.managers.GameObjectManager;
import com.riaanvo.planettanks.managers.LevelManager;

/**
 * This class creates and displays the levels that the player can play. It extends functionality from
 * the state superclass. It allows the player to play the levels and interact with the game ojects
 */

public class PlayState extends State {
    private LevelManager mLevelManager;
    private CameraController mCameraController;
    private GameObjectManager mGameObjectManager;

    //UI controls
    private Stage mStage;
    private Skin mSkin;
    private Touchpad mMovementTouchpad;
    private Touchpad mAimingTouchpad;
    private TextButton mMainMenuButton;
    private TextButton mFireButton;

    public PlayState() {
        mLevelManager = LevelManager.get();
        mGameObjectManager = GameObjectManager.get();
        mCameraController = CameraController.get();
    }

    @Override
    protected void update(float deltaTime) {
        //Update the game objects, camera, level manager and stage
        mGameObjectManager.update(deltaTime);
        mCameraController.update(deltaTime);
        mLevelManager.update(deltaTime);
        mStage.act();

        //TODO: remove this pc debugging code
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            mGameStateManager.pop();
        }

    }

    @Override
    protected void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        //Render all the game objects and the UI
        mGameObjectManager.render(spriteBatch, modelBatch);
        mStage.draw();
    }

    @Override
    protected void loaded() {
        //Load the level to be played
        mLevelManager.LoadLevel();

        mStage = new Stage(new ScalingViewport(Scaling.stretch, Constants.VIRTUAL_SCREEN_WIDTH, Constants.VIRTUAL_SCREEN_HEIGHT));
        mSkin = mContentManager.getSkin(Constants.SKIN_KEY);

        //Set up the touch pads
        float touchpadSize = 200f;
        float touchpadPadding = 30f;
        float touchpadDeadZone = 10f;
        mMovementTouchpad = new Touchpad(touchpadDeadZone, mSkin, "transparent");
        mMovementTouchpad.setBounds(touchpadPadding, touchpadPadding, touchpadSize, touchpadSize);

        mAimingTouchpad = new Touchpad(touchpadDeadZone, mSkin, "transparent");
        mAimingTouchpad.setBounds(mStage.getWidth() - touchpadSize - touchpadPadding, touchpadPadding, touchpadSize, touchpadSize);

        //Set up the buttons
        float buttonPadding = 80;
        mMainMenuButton = new TextButton("||", mSkin, "transparent");
        mMainMenuButton.setBounds(0, mStage.getHeight() - buttonPadding, buttonPadding, buttonPadding);
        mMainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pauseGame();
            }
        });

        mFireButton = new TextButton("FIRE", mSkin, "transparent");
        mFireButton.setBounds(mStage.getWidth() - (buttonPadding + touchpadPadding), touchpadPadding + touchpadSize, buttonPadding, buttonPadding);
        mFireButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mLevelManager.getPlayer().shoot();
            }
        });


        //Add the touchpads and buttons to the stage
        mStage.addActor(mMainMenuButton);
        mStage.addActor(mFireButton);
        mStage.addActor(mMovementTouchpad);
        mStage.addActor(mAimingTouchpad);
    }

    /**
     * Add a pause state on top and pause the game
     */
    public void pauseGame() {
        mGameStateManager.push(new PauseState());
    }

    @Override
    public void initialiseInput() {
        if (mStage == null) return;
        Gdx.input.setInputProcessor(mStage);
        mStage.act(0.01f);
        //Pass the touchpads to the player
        mLevelManager.getPlayer().setTouchPads(mMovementTouchpad, mAimingTouchpad);
    }

    @Override
    public void dispose() {
        mStage.dispose();
        LevelManager.get().unloadLevel();

    }
}
