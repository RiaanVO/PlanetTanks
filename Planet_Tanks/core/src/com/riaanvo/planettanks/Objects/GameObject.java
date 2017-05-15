package com.riaanvo.planettanks.Objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by riaanvo on 12/5/17.
 */

public abstract class GameObject {
    protected Vector3 mPosition;
    protected float mOrientation;
    protected Vector3 mVelocity;
    protected float mAngularVelocity;

    public GameObject(){
        this(Vector3.Zero);
    }

    public GameObject(Vector3 position){
        this(position, 0f);
    }

    public GameObject(Vector3 position, float orientation){
        mPosition = position;
        mOrientation = orientation;
        mVelocity = Vector3.Zero;
        mAngularVelocity = 0;
    }


    public abstract void update(float dt);

    public abstract void render(SpriteBatch spriteBatch, ModelBatch modelBatch);


    public Vector3 getPosition() {
        return mPosition;
    }

    public void setPosition(Vector3 position) {
        mPosition = position;
    }

    public float getOrientation() {
        return mOrientation;
    }

    public void setOrientation(float orientation) {
        mOrientation = orientation;
    }

    public Vector3 getVelocity() {
        return mVelocity;
    }

    public void setVelocity(Vector3 velocity) {
        mVelocity = velocity;
    }

    public float getAngularVelocity() {
        return mAngularVelocity;
    }

    public void setAngularVelocity(float angularVelocity) {
        mAngularVelocity = angularVelocity;
    }
}
