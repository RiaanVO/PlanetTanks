package com.riaanvo.planettanks.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.GameObjects.CameraController;
import com.riaanvo.planettanks.GameObjects.Player;
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
    private Touchpad.TouchpadStyle touchpadStyle;
    private Skin touchpadSkin;
    private Touchpad movementTouchpad;
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
        if(mLevelManager.isLevelToLoadSet()){
            mLevelManager.loadSetLevel();
        } else {
            mLevelManager.LoadLevel(0);
        }

        mStage = new Stage(new ScalingViewport(Scaling.stretch, Constants.VIRTUAL_SCREEN_WIDTH, Constants.VIRTUAL_SCREEN_HEIGHT));

        touchpadSkin = new Skin();//mContentManager.getSkin(Constants.SKIN_KEY);
        //if(touchpadSkin.getDrawable("touchBackground") == null || touchpadSkin.getDrawable("touchKnob") == null) {
            touchpadSkin.add("touchBackground", mContentManager.getTexture(Constants.TOUCHPAD_BACKGROUND));
            touchpadSkin.add("touchKnob", mContentManager.getTexture(Constants.TOUCHPAD_KNOB));
        //}

        touchpadStyle = new Touchpad.TouchpadStyle();
        touchpadStyle.background = touchpadSkin.getDrawable("touchBackground");
        touchpadStyle.knob = touchpadSkin.getDrawable("touchKnob");

        float touchpadSize = 200f;
        float touchpadPadding = 15;

        movementTouchpad = new Touchpad(10f, touchpadStyle);
        movementTouchpad.setBounds(touchpadPadding, touchpadPadding, touchpadSize, touchpadSize);

        aimingTouchpad = new Touchpad(50f, touchpadStyle);
        aimingTouchpad.setBounds(mStage.getWidth() - touchpadSize - touchpadPadding, touchpadPadding, touchpadSize, touchpadSize);
        aimingTouchpad.addListener(new ActorGestureListener(){
            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {
                super.tap(event, x, y, count, button);
                mLevelManager.getPlayer().shoot();
            }
        });

        mStage.addActor(movementTouchpad);
        mStage.addActor(aimingTouchpad);
    }

    @Override
    public void initialiseInput() {
        if(mStage == null) return;
        Gdx.input.setInputProcessor(mStage);
        mLevelManager.getPlayer().setTouchPads(movementTouchpad, aimingTouchpad);
    }

    @Override
    public void dispose() {
        mStage.dispose();
        touchpadSkin.dispose();
        LevelManager.get().unloadLevel();

        if (touchpadSkin != null)
            touchpadSkin.dispose();
    }


}
