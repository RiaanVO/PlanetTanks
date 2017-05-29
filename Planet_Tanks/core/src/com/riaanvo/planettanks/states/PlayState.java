package com.riaanvo.planettanks.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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
    private Touchpad movementTouchpad;
    private Touchpad.TouchpadStyle touchpadStyle;
    private Skin touchpadSkin;

    private Touchpad aimingTouchpad;

    public PlayState() {
        mLevelManager = LevelManager.get();
        mGameObjectManager = GameObjectManager.get();
        mCameraController = CameraController.get();

        mContentManager.loadTexture(Constants.FLOOR_TEXTURE);

        mContentManager.loadModel(Constants.BASIC_TANK_BODY_MODEL);
        mContentManager.loadModel(Constants.BASIC_TANK_TURRET_MODEL);

        mContentManager.loadModel(Constants.SIMPLE_SPIKES_SPIKES);
        mContentManager.loadModel(Constants.SIMPLE_SPIKES_BASE);

        mContentManager.loadTexture(Constants.TOUCHPAD_BACKGROUND);
        mContentManager.loadTexture(Constants.TOUCHPAD_KNOB);
    }

    @Override
    protected void update(float deltaTime) {
        mGameObjectManager.update(deltaTime);
        mCameraController.update(deltaTime);
        mStage.act();

        mLevelManager.update(deltaTime);
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            mGameStateManager.push(new TransitionState(null, TransitionState.TransitionType.BLACK_FADE_REMOVE));
        }
    }

    @Override
    protected void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        mGameObjectManager.render(spriteBatch, modelBatch);
        mStage.draw();
    }

    @Override
    protected void loaded() {
        mLevelManager.LoadLevel(0);

        mStage = new Stage(new ScreenViewport());
        //setupPlayerInput();
    }

    private void setupPlayerInput() {
        touchpadSkin = new Skin();
        touchpadSkin.add("touchBackground", mContentManager.getTexture(Constants.TOUCHPAD_BACKGROUND));
        touchpadSkin.add("touchKnob", mContentManager.getTexture(Constants.TOUCHPAD_KNOB));
        touchpadStyle = new Touchpad.TouchpadStyle();
        touchpadStyle.background = touchpadSkin.getDrawable("touchBackground");
        touchpadStyle.knob = touchpadSkin.getDrawable("touchKnob");

        float touchpadSize = 200f;
        float touchpadPadding = mStage.getWidth() * 0.01f;
        float deadZoneRadius = 10f;
        movementTouchpad = new Touchpad(deadZoneRadius, touchpadStyle);
        movementTouchpad.setBounds(touchpadPadding, touchpadPadding, touchpadSize, touchpadSize);
        movementTouchpad.setScale(2, 2);

        aimingTouchpad = new Touchpad(deadZoneRadius, touchpadStyle);
        aimingTouchpad.setBounds(mStage.getWidth() - touchpadSize - touchpadPadding, touchpadPadding, touchpadSize, touchpadSize);
        aimingTouchpad.setScale(2, 2);

        mStage.addActor(movementTouchpad);
        mStage.addActor(aimingTouchpad);
        mLevelManager.getPlayer().setTouchPads(movementTouchpad, aimingTouchpad);
    }

    @Override
    public void initialiseInput() {
        if(mStage == null) return;
        Gdx.input.setInputProcessor(mStage);
    }

    @Override
    public void dispose() {
        mStage.dispose();
        LevelManager.get().unloadLevel();

        if (touchpadSkin != null)
            touchpadSkin.dispose();
    }


}
