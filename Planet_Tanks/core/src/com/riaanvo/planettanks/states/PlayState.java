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
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.Objects.CameraController;
import com.riaanvo.planettanks.Objects.FloorTile;
import com.riaanvo.planettanks.Objects.GameObject;
import com.riaanvo.planettanks.Objects.Player;
import com.riaanvo.planettanks.Objects.WallSegment;

import java.util.LinkedList;

/**
 * Created by riaanvo on 9/5/17.
 */

public class PlayState extends State {

    private Model cube;

    private Player player;
    private CameraController mCameraController;

    private LinkedList<GameObject> mGameObjects = new LinkedList<GameObject>();

    //For the floor
    private ModelBuilder modelBuilder;
    private Model floor;
    private ModelInstance[][] floorInstances;

    public PlayState(){
        mContentManager.loadTexture(Constants.FLOOR_TEXTURE);
        mContentManager.loadModel(Constants.BASIC_TANK_BODY_MODEL);
        mContentManager.loadModel(Constants.BASIC_TANK_TURRET_MODEL);
    }

    @Override
    protected void update(float deltaTime) {
        //player.update(deltaTime);

        for(GameObject gameObject : mGameObjects){
            gameObject.update(deltaTime);
        }

        mCameraController.update(deltaTime);
    }

    @Override
    protected void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        //player.render(spriteBatch, modelBatch);

        for(GameObject gameObject : mGameObjects){
            gameObject.render(spriteBatch, modelBatch);
        }

//        modelBatch.begin(mCameraController.getCamera());
//        for(int y = 0; y < floorInstances.length; y ++){
//            for(int x = 0; x < floorInstances[0].length; x ++){
//                modelBatch.render(floorInstances[y][x]);
//            }
//        }
//        modelBatch.end();

    }

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

    @Override
    protected void loaded() {
        mCameraController = CameraController.get();
        mCameraController.CreatePerspective(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1f, 300f, new Vector3(0,0,0), new Vector3(0,20,10));

        player = new Player(mContentManager.getModel(Constants.BASIC_TANK_BODY_MODEL), mContentManager.getModel(Constants.BASIC_TANK_TURRET_MODEL));
        player.setPosition(new Vector3(5,0,5));
        mGameObjects.add(player);

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
                    System.out.println("Adding floor: " + position);
                    mGameObjects.add(new FloorTile(floor, position));
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
                    System.out.println("Adding wall: " + position);
                    mGameObjects.add(new WallSegment(cube, position));
                }
            }
        }

    }

    @Override
    public void dispose() {
        cube.dispose();
        floor.dispose();
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
