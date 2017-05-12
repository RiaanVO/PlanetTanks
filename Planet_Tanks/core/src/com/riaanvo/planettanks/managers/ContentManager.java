package com.riaanvo.planettanks.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
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

    private ContentManager(){
        mAssetManager = new AssetManager();
    }

    public void addModel(String key){
        mAssetManager.load(key, Model.class);
    }

    public Model getModel(String key){
        return mAssetManager.get(key, Model.class);
    }

    public void addTextureAtlas(String key){
        mAssetManager.load(key, TextureAtlas.class);
    }

    public TextureAtlas getTextureAtlas(String key){
        return mAssetManager.get(key, TextureAtlas.class);
    }

    public void addTexture(String key){
        mAssetManager.load(key, Texture.class);
    }

    public Texture getTexture(String key){
        return mAssetManager.get(key, Texture.class);
    }

    public void addSkin(String key){
        mAssetManager.load(key, Skin.class);
    }

    public Skin getSkin(String key){
        return mAssetManager.get(key, Skin.class);
    }

    public void addBitmapFont(String key){
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
    }
}
