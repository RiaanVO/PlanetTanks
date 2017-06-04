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
 * Created by riaanvo on 9/5/17.
 */

public class PlayState extends State {
    private LevelManager mLevelManager;
    private CameraController mCameraController;
    private GameObjectManager mGameObjectManager;

    //UI controls
    private Stage mStage;
    private Skin mSkin;
    private Touchpad movementTouchpad;
    private Touchpad aimingTouchpad;
    private TextButton mainMenuButton;
    private TextButton fireButton;

    public PlayState() {
        mLevelManager = LevelManager.get();
        mGameObjectManager = GameObjectManager.get();
        mCameraController = CameraController.get();
    }

    @Override
    protected void update(float deltaTime) {
        mGameObjectManager.update(deltaTime);
        mCameraController.update(deltaTime);
        mStage.act();

        mLevelManager.update(deltaTime);
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            mGameStateManager.pop();
        }

    }

    @Override
    protected void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        mGameObjectManager.render(spriteBatch, modelBatch);
        mStage.draw();
    }

    @Override
    protected void loaded() {
        mLevelManager.LoadLevel();

        mStage = new Stage(new ScalingViewport(Scaling.stretch, Constants.VIRTUAL_SCREEN_WIDTH, Constants.VIRTUAL_SCREEN_HEIGHT));
        mSkin = mContentManager.getSkin(Constants.SKIN_KEY);

        float touchpadSize = 200f;
        float touchpadPadding = 30f;
        float touchpadDeadZone = 10f;
        movementTouchpad = new Touchpad(touchpadDeadZone, mSkin, "transparent");
        movementTouchpad.setBounds(touchpadPadding, touchpadPadding, touchpadSize, touchpadSize);

        aimingTouchpad = new Touchpad(touchpadDeadZone, mSkin, "transparent");
        aimingTouchpad.setBounds(mStage.getWidth() - touchpadSize - touchpadPadding, touchpadPadding, touchpadSize, touchpadSize);

        float buttonPadding = 80;
        mainMenuButton = new TextButton("||", mSkin, "transparent");
        mainMenuButton.setBounds(0, mStage.getHeight() - buttonPadding, buttonPadding, buttonPadding);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pauseGame();
            }
        });

        fireButton = new TextButton("FIRE", mSkin, "transparent");
        fireButton.setBounds(mStage.getWidth() - (buttonPadding + touchpadPadding), touchpadPadding + touchpadSize, buttonPadding, buttonPadding);
        fireButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mLevelManager.getPlayer().shoot();
            }
        });


        mStage.addActor(mainMenuButton);
        mStage.addActor(fireButton);

        mStage.addActor(movementTouchpad);
        mStage.addActor(aimingTouchpad);
    }

    public void pauseGame() {
        mGameStateManager.push(new PauseState());
    }

    @Override
    public void initialiseInput() {
        if (mStage == null) return;
        Gdx.input.setInputProcessor(mStage);
        mLevelManager.getPlayer().setTouchPads(movementTouchpad, aimingTouchpad);
    }

    @Override
    public void dispose() {
        mStage.dispose();
        LevelManager.get().unloadLevel();

    }


}
