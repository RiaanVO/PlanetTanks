package com.riaanvo.planettanks.Objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by riaanvo on 12/5/17.
 */

public class TankBody extends GameObject {
    private ModelInstance mModelInstance;

    public TankBody(ModelInstance modelInstance){
        this(modelInstance, Vector3.Zero);
    }

    public TankBody(ModelInstance modelInstance, Vector3 position){
        this(modelInstance, position, 0f);
    }

    public TankBody(ModelInstance modelInstance, Vector3 position, float orientation){
        super(position, orientation);
        mModelInstance = modelInstance;
        mModelInstance.transform.set(position, new Quaternion(Vector3.Y, orientation));
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {

    }
}
