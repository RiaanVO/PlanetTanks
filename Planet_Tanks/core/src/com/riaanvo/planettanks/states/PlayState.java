package com.riaanvo.planettanks.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.Objects.BasicEnemy;
import com.riaanvo.planettanks.Objects.CameraController;
import com.riaanvo.planettanks.Objects.FloorTile;
import com.riaanvo.planettanks.Objects.GameObject;
import com.riaanvo.planettanks.Objects.Player;
import com.riaanvo.planettanks.Objects.TankController;
import com.riaanvo.planettanks.Objects.WallSegment;
import com.riaanvo.planettanks.managers.CollisionManager;
import com.riaanvo.planettanks.managers.GameObjectManager;

import java.util.LinkedList;

/**
 * Created by riaanvo on 9/5/17.
 */

public class PlayState extends State {
    private Player player;
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
            {0,1,1,1,1,1,1,1,1,0},
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
        mGameObjectManager = GameObjectManager.get();
        mCameraController = CameraController.get();
        mCameraController.CreatePerspective(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1f, 300f, new Vector3(0,0,0), new Vector3(0,20,10));

        mContentManager.loadTexture(Constants.FLOOR_TEXTURE);
        mContentManager.loadModel(Constants.BASIC_TANK_BODY_MODEL);
        mContentManager.loadModel(Constants.BASIC_TANK_TURRET_MODEL);

        mContentManager.loadTexture(Constants.TOUCHPAD_BACKGROUND);
        mContentManager.loadTexture(Constants.TOUCHPAD_KNOB);
    }

    @Override
    protected void update(float deltaTime) {
        mGameObjectManager.update(deltaTime);

        mCameraController.update(deltaTime);

        mStage.act();
    }

    @Override
    protected void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        mGameObjectManager.render(spriteBatch, modelBatch);

        mStage.draw();
    }

    @Override
    protected void loaded() {

        ColorAttribute playerColour = new ColorAttribute(ColorAttribute.Diffuse, Color.BLUE);
        TankController playerTank = new TankController(mContentManager.getModel(Constants.BASIC_TANK_BODY_MODEL), mContentManager.getModel(Constants.BASIC_TANK_TURRET_MODEL), playerColour);
        player = new Player(playerTank);
        player.setPosition(new Vector3(5,0,5));
        mGameObjectManager.addGameObject(player);
        mCameraController.setTrackingObject(player);

        float tileSize = 2;
        Model floorPlane = mContentManager.getFloorPlane();
        TextureAttribute textureAttribute = new TextureAttribute(TextureAttribute.Diffuse, mContentManager.getTexture(Constants.FLOOR_TEXTURE));
        floorPlane.materials.get(0).set(textureAttribute);

        for(int z = 0; z < floorMap.length; z++){
            for(int x = 0; x < floorMap[0].length; x ++){
                if(floorMap[z][x] == 1){
                    Vector3 position = new Vector3(tileSize/2 + tileSize * x, 0, tileSize/2 + tileSize * z);
                    mGameObjectManager.addGameObject(new FloorTile(floorPlane, position));
                }
            }
        }

        for(int z = 0; z < wallMap.length; z++){
            for(int x = 0; x < wallMap[0].length; x ++){
                if(wallMap[z][x] == 1){
                    Vector3 position = new Vector3(tileSize/2 + tileSize * x, tileSize/2, tileSize/2 + tileSize * z);
                    mGameObjectManager.addGameObject(new WallSegment(mContentManager.getWallSegment(), position, new Vector3(tileSize, tileSize, tileSize)));
                }
            }
        }

        for(int z = 0; z < entityMap.length; z++){
            for(int x = 0; x < entityMap[0].length; x ++){
                if(entityMap[z][x] == 1){
                    Vector3 position = new Vector3(tileSize/2 + tileSize * x, 0, tileSize/2 + tileSize * z);

                    ColorAttribute enemyColour = new ColorAttribute(ColorAttribute.Diffuse, Color.RED);
                    TankController enemyTank = new TankController(mContentManager.getModel(Constants.BASIC_TANK_BODY_MODEL), mContentManager.getModel(Constants.BASIC_TANK_TURRET_MODEL), enemyColour);
                    BasicEnemy enemy = new BasicEnemy(enemyTank);
                    enemy.setPosition(position);
                    mGameObjectManager.addGameObject(enemy);
                }
            }
        }

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
        player.setTouchPads(movementTouchpad, aimingTouchpad);
    }

    @Override
    public void dispose() {
        mStage.dispose();
        mGameObjectManager.clearGameObjects();
        CollisionManager.get().clearColliders();
        CollisionManager.get().dispose();

        if(touchpadSkin != null)
            touchpadSkin.dispose();
    }


}
