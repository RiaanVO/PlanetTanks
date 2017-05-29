package com.riaanvo.planettanks.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.GameObjects.BasicEnemy;
import com.riaanvo.planettanks.GameObjects.CameraController;
import com.riaanvo.planettanks.GameObjects.FloorTile;
import com.riaanvo.planettanks.GameObjects.Player;
import com.riaanvo.planettanks.GameObjects.SimpleSpikes;
import com.riaanvo.planettanks.GameObjects.TankController;
import com.riaanvo.planettanks.GameObjects.WallSegment;
import com.riaanvo.planettanks.managers.CollisionManager;
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

    private int[][] floorMap = {
            {0,0,0,0,0,0,0,0,0,0},
            {0,1,1,1,1,1,1,1,1,0},
            {0,1,1,1,1,1,1,1,1,0},
            {0,1,1,1,1,1,1,1,1,0},
            {0,1,1,2,2,2,1,1,1,0},
            {0,1,1,1,1,1,1,1,1,0},
            {0,0,0,0,0,0,0,0,0,0}
    };

    private int[][] wallMap = {
            {1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,0,0,1,1,1,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1}
    };

    private int[][] entityMap = {
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,1,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0}
    };

    public PlayState(){
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
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
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
        mLevelManager.LoadLevel(0);

        mStage = new Stage(new ScreenViewport());
        //setupPlayerInput();
    }

    private void setupPlayerInput(){
        Gdx.input.setInputProcessor(mStage);

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
        movementTouchpad.setScale(2,2);

        aimingTouchpad = new Touchpad(deadZoneRadius, touchpadStyle);
        aimingTouchpad.setBounds(mStage.getWidth() - touchpadSize - touchpadPadding, touchpadPadding, touchpadSize, touchpadSize);
        aimingTouchpad.setScale(2,2);

        mStage.addActor(movementTouchpad);
        mStage.addActor(aimingTouchpad);
        mLevelManager.getPlayer().setTouchPads(movementTouchpad, aimingTouchpad);
    }

    @Override
    public void dispose() {
        mStage.dispose();
        LevelManager.get().unloadLevel();
        CollisionManager.get().dispose();

        if(touchpadSkin != null)
            touchpadSkin.dispose();
    }


}
