package com.riaanvo.planettanks.Physics;

import com.badlogic.gdx.math.Vector3;
import com.riaanvo.planettanks.Objects.GameObject;

/**
 * Created by riaanvo on 22/5/17.
 */

public class SphereCollider extends Collider {
    private BoundingSphere mBoundingSphere;

    public SphereCollider(GameObject gameObject, ColliderTag tag, Vector3 offSet, float radius){
        mColliderType = ColliderType.SPHERE;
        mGameObject = gameObject;
        mTag = tag;
        mOffset = offSet;
        mBoundingSphere = new BoundingSphere(gameObject.getPosition().cpy().add(offSet), radius);
    }

    public void updatePosition(){
        mBoundingSphere.setCenter(mGameObject.getPosition().cpy().add(mOffset));
    }

    public void setPosition(Vector3 newGameObjectPosition){
        mBoundingSphere.setCenter(newGameObjectPosition.cpy().add(mOffset));
    }

    public void adjustPosition(Vector3 adjustment){
        mBoundingSphere.setCenter(mBoundingSphere.getCenter().cpy().add(adjustment));
    }

    public void setRadius(float radius){
        mBoundingSphere.setRadius(radius);
    }

    @Override
    public boolean intersectsWith(Collider other) {
        switch (other.mColliderType){
            case BOX:
                return intersects(((BoxCollider)other).getBoundingBox(), mBoundingSphere);
            case SPHERE:
                return intersects(((SphereCollider)other).getBoundingSphere(), mBoundingSphere);
        }
        return false;
    }

    public BoundingSphere getBoundingSphere(){
        return mBoundingSphere;
    }
}
