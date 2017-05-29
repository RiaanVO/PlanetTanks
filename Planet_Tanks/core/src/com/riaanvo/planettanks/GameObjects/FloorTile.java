package com.riaanvo.planettanks.GameObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by riaanvo on 22/5/17.
 */

public class FloorTile extends GameObject {
    private ModelInstance mFloorTile;
    private com.riaanvo.planettanks.GameObjects.CameraController mCameraController;

    public FloorTile(Model floorTile, Vector3 position){
        mCameraController = com.riaanvo.planettanks.GameObjects.CameraController.get();
        mFloorTile = new ModelInstance(floorTile);
        mFloorTile.transform.rotate(Vector3.X, -90);
        mFloorTile.transform.setTranslation(position);
        setPosition(position);
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        modelBatch.begin(mCameraController.getCamera());
        modelBatch.render(mFloorTile);//, mCameraController.getEnvironment());
        modelBatch.end();
    }
}
