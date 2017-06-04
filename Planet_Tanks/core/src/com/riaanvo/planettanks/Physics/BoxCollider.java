/*
 * Copyright (C) 2017 Riaan Van Onselen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.riaanvo.planettanks.Physics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.riaanvo.planettanks.GameObjects.GameObject;

/**
 * Created by riaanvo on 22/5/17.
 */

public class BoxCollider extends Collider {
    private BoundingBox mBoundingBox;
    private Vector3 mSize;
    private Vector3 mHalfSize;


    public BoxCollider(GameObject gameObject, ColliderTag tag, Vector3 offset, Vector3 size) {
        mColliderType = ColliderType.BOX;
        mGameObject = gameObject;
        mTag = tag;
        mOffset = offset;
        setSize(size);
        mBoundingBox = new BoundingBox(getMinBound(), getMaxBound());
    }

    public void updatePosition() {
        mBoundingBox.set(getMinBound(), getMaxBound());
    }

    public void setSize(Vector3 newSize) {
        mSize = newSize;
        mHalfSize = newSize.cpy().scl(0.5f);
    }

    @Override
    public boolean intersectsWith(Collider other) {
        switch (other.mColliderType) {
            case BOX:
                return intersects(((BoxCollider) other).getBoundingBox(), mBoundingBox);
            case SPHERE:
                return intersects(mBoundingBox, ((SphereCollider) other).getBoundingSphere());
        }
        return false;
    }

    public Vector3 getMinBound() {
        return mGameObject.getPosition().cpy().add(mOffset).sub(mHalfSize);
    }

    public Vector3 getMaxBound() {
        return mGameObject.getPosition().cpy().add(mOffset).add(mHalfSize);
    }

    public BoundingBox getBoundingBox() {
        return mBoundingBox;
    }

    public Vector3 getSize() {
        return mSize;
    }
}
