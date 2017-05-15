package com.riaanvo.planettanks.Objects;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by riaanvo on 12/5/17.
 */

public class Tank extends GameObject{
    private ModelInstance mTankBase;
    private ModelInstance mTankTurret;

    public Tank(ModelInstance tankBase, ModelInstance tankTurret) {
        this(tankBase, tankTurret, Vector3.Zero);
    }

    public Tank(ModelInstance tankBase, ModelInstance tankTurret, Vector3 position) {
        this(tankBase, tankTurret, position, 0f);
    }

    public Tank(ModelInstance tankBase, ModelInstance tankTurret, Vector3 position, float orientation) {
        super(position, orientation);
        mTankBase = tankBase;
        mTankTurret = tankTurret;
        mTankBase.transform.set(position, new Quaternion(Vector3.Y, orientation));
        mTankTurret.transform.set(position, new Quaternion(Vector3.Y, orientation));
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {

    }
}
