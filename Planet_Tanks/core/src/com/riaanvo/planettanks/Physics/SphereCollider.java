package com.riaanvo.planettanks.Physics;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Sphere;
import com.riaanvo.planettanks.Objects.GameObject;

/**
 * Created by riaanvo on 22/5/17.
 */

public class SphereCollider extends Collider {
    private Sphere mSphereCollider;

    public SphereCollider(GameObject gameObject, ColliderTag tag, float radius){
        mGameObject = gameObject;
        mTag = tag;
        mSphereCollider = new Sphere(gameObject.getPosition(), radius);
    }

    public void updatePosition(){
        //mSphereCollider.center = mGameObject.getPosition();
    }

    @Override
    public boolean isColliding(BoundingBox other) {
        return false;
    }

    @Override
    public boolean isColliding(Sphere other) {
        return false;
    }
}
