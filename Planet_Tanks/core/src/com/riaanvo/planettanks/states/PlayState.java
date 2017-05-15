package com.riaanvo.planettanks.states;

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
import com.riaanvo.planettanks.Objects.Player;
import com.riaanvo.planettanks.managers.ContentManager;

/**
 * Created by riaanvo on 9/5/17.
 */

public class PlayState extends State {
    private ContentManager mContentManager;

    private Model cube;

    private Player player;
    private CameraController mCameraController;

    private ModelBuilder modelBuilder;
    private Model floor;
    private ModelInstance[][] floorInstances;


//        environment = new Environment();
//        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
//        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

    public PlayState(){

        mContentManager = ContentManager.get();
        ModelBuilder modelBuilder = new ModelBuilder();
        Material mat = new Material(ColorAttribute.createDiffuse(Color.GREEN));
        cube = modelBuilder.createBox(1f, 1f, 1f, mat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);


        mCameraController = CameraController.get();
        mCameraController.CreatePerspective();

        player = new Player(new ModelInstance(cube));
        player.setPosition(new Vector3(1,1,1));

        //mCameraController.setCameraPositionOffset(new Vector3(0,10,10));
        mCameraController.setTrackingObject(player);


        float floorTileSize = 2;
        Material material = new Material( new BlendingAttribute(1), new FloatAttribute(FloatAttribute.AlphaTest, 0.5f));
        floor = createPlaneModel(floorTileSize, floorTileSize, material, 0, 0, 1, 1);
        ColorAttribute colorAttr = new ColorAttribute(ColorAttribute.Diffuse, Color.WHITE);
        floor.materials.get(0).set(colorAttr);
        TextureAttribute textureAttribute = new TextureAttribute(TextureAttribute.Diffuse, mContentManager.getTexture(Constants.FLOOR_TEXTURE));
        floor.materials.get(0).set(textureAttribute);

        int floorNumWidth = 50;
        int floorNumHeight = 50;

        floorInstances = new ModelInstance[floorNumHeight][floorNumWidth];

        for(int y = 0; y < floorNumHeight; y++){
            for(int x = 0; x < floorNumWidth; x++){
                ModelInstance floorTile = new ModelInstance(floor);
                floorTile.transform.rotate(Vector3.X, -90);
                floorTile.transform.setTranslation(floorTileSize/2 + x * floorTileSize, 0 , -floorTileSize/2 - y * floorTileSize);
                floorInstances[y][x] = floorTile;

            }
        }
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

    @Override
    public void update(float deltaTime) {
        player.update(deltaTime);
        mCameraController.update(deltaTime);
    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        player.render(spriteBatch, modelBatch);

        modelBatch.begin(mCameraController.getCamera());
        for(int y = 0; y < floorInstances.length; y ++){
            for(int x = 0; x < floorInstances[0].length; x ++){
                modelBatch.render(floorInstances[y][x]);
            }
        }
        modelBatch.end();

    }

    @Override
    public void dispose() {
        cube.dispose();
        floor.dispose();
    }
}
