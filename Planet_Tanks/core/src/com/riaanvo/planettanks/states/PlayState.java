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
import com.riaanvo.planettanks.Objects.CameraController;
import com.riaanvo.planettanks.Objects.FloorTile;
import com.riaanvo.planettanks.Objects.GameObject;
import com.riaanvo.planettanks.Objects.Player;
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

    //For the floor
    private ModelBuilder modelBuilder;
    private Model floor;
    private Model cube;

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
            {1,0,0,1,1,1,1,0,0,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1}
    };



    public PlayState(){
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
        mGameObjectManager = GameObjectManager.get();
        mCameraController = CameraController.get();
        mCameraController.CreatePerspective(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1f, 300f, new Vector3(0,0,0), new Vector3(0,20,10));

        player = new Player(mContentManager.getModel(Constants.BASIC_TANK_BODY_MODEL), mContentManager.getModel(Constants.BASIC_TANK_TURRET_MODEL));
        player.setPosition(new Vector3(5,0,5));
        mGameObjectManager.addGameObject(player);

        mCameraController.setTrackingObject(player);

        float tileSize = 2;
        Material material = new Material( new BlendingAttribute(1), new FloatAttribute(FloatAttribute.AlphaTest, 0.5f));
        floor = createPlaneModel(tileSize, tileSize, material, 0, 0, 1, 1);
        ColorAttribute colorAttr = new ColorAttribute(ColorAttribute.Diffuse, Color.WHITE);
        floor.materials.get(0).set(colorAttr);
        TextureAttribute textureAttribute = new TextureAttribute(TextureAttribute.Diffuse, mContentManager.getTexture(Constants.FLOOR_TEXTURE));
        floor.materials.get(0).set(textureAttribute);

        for(int z = 0; z < floorMap.length; z++){
            for(int x = 0; x < floorMap[0].length; x ++){
                if(floorMap[z][x] == 1){
                    Vector3 position = new Vector3(tileSize/2 + tileSize * x, 0, tileSize/2 + tileSize * z);
                    mGameObjectManager.addGameObject(new FloorTile(floor, position));
                }
            }
        }

        ModelBuilder modelBuilder = new ModelBuilder();
        Material mat = new Material(ColorAttribute.createDiffuse(Color.GREEN));
        cube = modelBuilder.createBox(tileSize, tileSize, tileSize, mat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        for(int z = 0; z < wallMap.length; z++){
            for(int x = 0; x < wallMap[0].length; x ++){
                if(wallMap[z][x] == 1){
                    Vector3 position = new Vector3(tileSize/2 + tileSize * x, tileSize/2, tileSize/2 + tileSize * z);
                    mGameObjectManager.addGameObject(new WallSegment(cube, position, new Vector3(tileSize, tileSize, tileSize)));
                }
            }
        }



        mStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(mStage);

        touchpadSkin = new Skin();
        touchpadSkin.add("touchBackground", mContentManager.getTexture(Constants.TOUCHPAD_BACKGROUND));
        touchpadSkin.add("touchKnob", mContentManager.getTexture(Constants.TOUCHPAD_KNOB));
        touchpadStyle = new Touchpad.TouchpadStyle();
        touchpadStyle.background = touchpadSkin.getDrawable("touchBackground");
        touchpadStyle.knob = touchpadSkin.getDrawable("touchKnob");

        float touchpadSize = 200f;//mStage.getWidth() * 0.1f;
        float touchpadPadding = mStage.getWidth() * 0.01f;
        float deadZoneRadius = 10f;
        movementTouchpad = new Touchpad(deadZoneRadius, touchpadStyle);
        movementTouchpad.setBounds(touchpadPadding, touchpadPadding, touchpadSize, touchpadSize);
        movementTouchpad.setScale(2,2);

        aimingTouchpad = new Touchpad(deadZoneRadius, touchpadStyle);
        aimingTouchpad.setBounds(mStage.getWidth() - touchpadSize - touchpadPadding, touchpadPadding, touchpadSize, touchpadSize);
        aimingTouchpad.setScale(2,2);

        //mStage.addActor(movementTouchpad);
        //mStage.addActor(aimingTouchpad);

        //player.setTouchPads(movementTouchpad, aimingTouchpad);
    }

    @Override
    public void dispose() {
        cube.dispose();
        floor.dispose();
        mStage.dispose();
        touchpadSkin.dispose();
        mGameObjectManager.clearGameObjects();
        CollisionManager.get().clearColliders();
        CollisionManager.get().dispose();
    }

    private Model createPlaneModel(final float width, final float height, final Material material, final float u1, final float v1, final float u2, final float v2) {
        modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder bPartBuilder = modelBuilder.part("rect", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, material);
        //NOTE ON TEXTURE REGION, MAY FILL OTHER REGIONS, USE GET region.getU() and so on
        bPartBuilder.setUVRange(u1, v1, u2, v2);
        bPartBuilder.rect(
                -(width*0.5f), -(height*0.5f), 0,
                (width*0.5f), -(height*0.5f), 0,
                (width*0.5f), (height*0.5f), 0,
                -(width*0.5f), (height*0.5f), 0,
                0, 0, -1);


        return (modelBuilder.end());
    }
}
