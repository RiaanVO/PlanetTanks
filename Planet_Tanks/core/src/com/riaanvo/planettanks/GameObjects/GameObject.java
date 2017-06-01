package com.riaanvo.planettanks.GameObjects;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.riaanvo.planettanks.Constants;

/**
 * Created by riaanvo on 12/5/17.
 */

public abstract class GameObject {
    protected String mTag;
    protected Vector3 mPosition;
    protected float mOrientation;
    protected Vector3 mVelocity;
    protected float mAngularVelocity;

    public GameObject() {
        this(Vector3.Zero);
    }

    public GameObject(Vector3 position) {
        this(position, 0f);
    }

    public GameObject(Vector3 position, float orientation) {
        mPosition = position;
        mOrientation = orientation;
        mVelocity = Vector3.Zero;
        mAngularVelocity = 0;
        mTag = "GameObject";
    }

    public abstract void update(float dt);

    public abstract void render(SpriteBatch spriteBatch, ModelBatch modelBatch);


    //https://xoppa.github.io/blog/3d-frustum-culling-with-libgdx/
    public boolean isVisible(){
            Camera cam = CameraController.get().getCamera();
            return cam.frustum.boundsInFrustum(mPosition, Constants.RENDER_BOUNDS);
    }

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

    protected float calculateOrientation(Vector3 direction) {
        float newOrientation;
        if (direction.x != 0) {
            if (direction.x < 0) {
                newOrientation = 360 - (float) Math.toDegrees(Math.atan2(direction.x, direction.z)) * -1;
            } else {
                newOrientation = (float) Math.toDegrees(Math.atan2(direction.x, direction.z));
            }
        } else {
            if (direction.z > 0) {
                newOrientation = 0;
            } else {
                newOrientation = 180;
            }
        }
        return newOrientation;
    }

    protected Vector3 calculateDirection(float orientation) {
        float castOrientation = Math.abs(orientation);
        boolean negX = false;
        boolean negZ = false;
        boolean normalCalc = true;
        float x;
        float z;
        if (castOrientation > 270) {
            castOrientation = 360 - castOrientation;
            negX = true;
        } else if (castOrientation > 180) {
            castOrientation = 270 - castOrientation;
            negZ = true;
            negX = true;
            normalCalc = false;
        } else if (castOrientation > 90) {
            castOrientation = 180 - castOrientation;
            negZ = true;
        }

        if (normalCalc) {
            x = (float) Math.sin(Math.toRadians(castOrientation));
            z = (float) Math.cos(Math.toRadians(castOrientation));
        } else {
            x = (float) Math.cos(Math.toRadians(castOrientation));
            z = (float) Math.sin(Math.toRadians(castOrientation));
        }

        if (negX) x *= -1;
        if (negZ) z *= -1;

        return new Vector3(x, 0, z);
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

    protected void setTag(String newTag) {
        mTag = newTag;
    }

    public String getTag() {
        return mTag;
    }

    public boolean compareTag(String testTag) {
        return mTag.equals(testTag);
    }
}
