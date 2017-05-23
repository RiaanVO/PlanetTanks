package com.riaanvo.planettanks.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.HashMap;

/**
 * Created by riaanvo on 8/5/17.
 */

public class ContentManager {
    private AssetManager mAssetManager;

    private static ContentManager sContentManager;
    public static ContentManager get(){
        if(sContentManager == null) sContentManager = new ContentManager();
        return sContentManager;
    }

    private Model shell;
    public Model getShell(){
        return shell;
    }

    private ContentManager(){
        mAssetManager = new AssetManager();

        ModelBuilder modelBuilder = new ModelBuilder();
        Material mat = new Material(ColorAttribute.createDiffuse(Color.RED));
        shell = modelBuilder.createBox(0.2f, 0.2f, 0.5f, mat, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }

    public void loadModel(String key){
        mAssetManager.load(key, Model.class);
    }

    public Model getModel(String key){
        return mAssetManager.get(key, Model.class);
    }

    public void loadTextureAtlas(String key){
        mAssetManager.load(key, TextureAtlas.class);
    }

    public TextureAtlas getTextureAtlas(String key){
        return mAssetManager.get(key, TextureAtlas.class);
    }

    public void loadTexture(String key){
        mAssetManager.load(key, Texture.class);
    }

    public Texture getTexture(String key){
        return mAssetManager.get(key, Texture.class);
    }

    public void loadSkin(String key){
        mAssetManager.load(key, Skin.class);
    }

    public Skin getSkin(String key){
        return mAssetManager.get(key, Skin.class);
    }

    public void loadBitmapFont(String key){
        mAssetManager.load(key, BitmapFont.class);
    }

    public BitmapFont getBitmapFont(String key){
        return mAssetManager.get(key, BitmapFont.class);
    }

    public boolean assetManagerUpdate(){
        return mAssetManager.update();
    }

    public AssetManager getAssetManager(){
        return mAssetManager;
    }
    /**
     * Dispose of all the assets loaded into the content manager
     */
    public void dispose(){
        mAssetManager.dispose();

        shell.dispose();
    }
}
