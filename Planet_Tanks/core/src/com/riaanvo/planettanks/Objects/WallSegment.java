package com.riaanvo.planettanks.Objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by riaanvo on 22/5/17.
 */

public class WallSegment extends GameObject {
    private ModelInstance mWallSegment;
    private CameraController mCameraController;

    public WallSegment(Model wallSegment, Vector3 position){
        mCameraController = CameraController.get();
        mWallSegment = new ModelInstance(wallSegment);
        mWallSegment.transform.setTranslation(position);
        setPosition(position);
    }


    @Override
    public void update(float dt) {
    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        modelBatch.begin(mCameraController.getCamera());
        modelBatch.render(mWallSegment, mCameraController.getEnvironment());
        modelBatch.end();
    }
}
