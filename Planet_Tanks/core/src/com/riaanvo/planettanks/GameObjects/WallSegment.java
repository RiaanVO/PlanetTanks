package com.riaanvo.planettanks.GameObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.riaanvo.planettanks.Physics.BoxCollider;
import com.riaanvo.planettanks.Physics.Collider;
import com.riaanvo.planettanks.managers.CollisionManager;

/**
 * Created by riaanvo on 22/5/17.
 */

public class WallSegment extends GameObject {
    private ModelInstance mWallSegment;
    private CameraController mCameraController;
    private BoxCollider mBoxCollider;

    public WallSegment(Model wallSegment, Vector3 position, Vector3 size) {
        mCameraController = CameraController.get();
        mWallSegment = new ModelInstance(wallSegment);
        mWallSegment.transform.setTranslation(position);
        setPosition(position);

        //GameObject gameObject, ColliderTag tag, Vector3 offset, Vector3 size -- size.cpy().scl(0.5f)
        mBoxCollider = new BoxCollider(this, Collider.ColliderTag.WALL, new Vector3(0, 0, 0), size);
        CollisionManager.get().addCollider(mBoxCollider);
    }


    @Override
    public void update(float dt) {
    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        modelBatch.begin(mCameraController.getCamera());
        modelBatch.render(mWallSegment, mCameraController.getEnvironment());
        modelBatch.end();

        CollisionManager.get().renderCollider(mBoxCollider);
    }
}
