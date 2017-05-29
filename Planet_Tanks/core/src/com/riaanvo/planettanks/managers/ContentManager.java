package com.riaanvo.planettanks.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.riaanvo.planettanks.Constants;

/**
 * Created by riaanvo on 8/5/17.
 */

public class ContentManager {
    private AssetManager mAssetManager;

    private static ContentManager sContentManager;

    public static ContentManager get() {
        if (sContentManager == null) sContentManager = new ContentManager();
        return sContentManager;
    }

    private ModelBuilder modelBuilder;
    private Model shell;

    public Model getShell() {
        return shell;
    }

    private Model wallSegment;

    public Model getWallSegment() {
        return wallSegment;
    }

    private Model floorPlane;

    public Model getFloorPlane() {
        return floorPlane;
    }

    private Model createPlaneModel(final float width, final float height, final Material material, final float u1, final float v1, final float u2, final float v2) {
        modelBuilder.begin();
        MeshPartBuilder bPartBuilder = modelBuilder.part("rect", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates, material);
        //NOTE ON TEXTURE REGION, MAY FILL OTHER REGIONS, USE GET region.getU() and so on
        bPartBuilder.setUVRange(u1, v1, u2, v2);
        bPartBuilder.rect(
                -(width * 0.5f), -(height * 0.5f), 0,
                (width * 0.5f), -(height * 0.5f), 0,
                (width * 0.5f), (height * 0.5f), 0,
                -(width * 0.5f), (height * 0.5f), 0,
                0, 0, -1);
        return (modelBuilder.end());
    }

    private ContentManager() {
        mAssetManager = new AssetManager();

        modelBuilder = new ModelBuilder();
        Material shellMat = new Material(ColorAttribute.createDiffuse(Color.RED));
        shell = modelBuilder.createBox(0.2f, 0.2f, 0.5f, shellMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        Material wallSegmentMat = new Material(ColorAttribute.createDiffuse(Color.GREEN));
        wallSegment = modelBuilder.createBox(Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE, wallSegmentMat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        Material floorMat = new Material(new BlendingAttribute(1), new FloatAttribute(FloatAttribute.AlphaTest, 0.5f));
        floorPlane = createPlaneModel(Constants.TILE_SIZE, Constants.TILE_SIZE, floorMat, 0, 0, 1, 1);
        ColorAttribute colorAttr = new ColorAttribute(ColorAttribute.Diffuse, Color.WHITE);
        floorPlane.materials.get(0).set(colorAttr);
    }

    public void loadModel(String key) {
        mAssetManager.load(key, Model.class);
    }

    public Model getModel(String key) {
        return mAssetManager.get(key, Model.class);
    }

    public void loadTextureAtlas(String key) {
        mAssetManager.load(key, TextureAtlas.class);
    }

    public TextureAtlas getTextureAtlas(String key) {
        return mAssetManager.get(key, TextureAtlas.class);
    }

    public void loadTexture(String key) {
        mAssetManager.load(key, Texture.class);
    }

    public Texture getTexture(String key) {
        return mAssetManager.get(key, Texture.class);
    }

    public void loadSkin(String key) {
        mAssetManager.load(key, Skin.class);
    }

    public Skin getSkin(String key) {
        return mAssetManager.get(key, Skin.class);
    }

    public void loadBitmapFont(String key) {
        mAssetManager.load(key, BitmapFont.class);
    }

    public BitmapFont getBitmapFont(String key) {
        return mAssetManager.get(key, BitmapFont.class);
    }

    public boolean assetManagerUpdate() {
        return mAssetManager.update();
    }

    public AssetManager getAssetManager() {
        return mAssetManager;
    }

    public void dispose() {
        mAssetManager.dispose();

        shell.dispose();
        floorPlane.dispose();
        wallSegment.dispose();
    }
}
