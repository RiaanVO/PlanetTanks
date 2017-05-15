package com.riaanvo.planettanks.managers;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.riaanvo.planettanks.Objects.GameObject;

import java.util.LinkedList;

/**
 * Created by riaanvo on 12/5/17.
 */

public class LevelManager {
    private Camera mCamera;
    private LinkedList<GameObject> mGameObjects = new LinkedList<GameObject>();

    private LevelManager sLevelManager;
    public LevelManager get(){
        if(sLevelManager == null){
            sLevelManager = new LevelManager();
        }
        return sLevelManager;
    }

    public void update(float dt){
        for (GameObject gameObject: mGameObjects) {
            gameObject.update(dt);
        }

    }

    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch){
        for (GameObject gameObject: mGameObjects) {
            gameObject.render(spriteBatch, modelBatch);
        }
    }

    public void setCamera(Camera camera){
        mCamera = camera;
    }

    public Camera getCamera(){
        return mCamera;
    }

    public void addGameObject(GameObject gameObject){
        mGameObjects.add(gameObject);
    }

    public void removeGameObject(GameObject gameObject){
        mGameObjects.remove(gameObject);
    }

    public void clearGameObjects(){
        mGameObjects.clear();
    }
}
