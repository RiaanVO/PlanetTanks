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
import com.riaanvo.planettanks.GameObjects.GameObject;

/**
 * Created by riaanvo on 22/5/17.
 */

public class SphereCollider extends Collider {
    private BoundingSphere mBoundingSphere;

    public SphereCollider(GameObject gameObject, ColliderTag tag, Vector3 offSet, float radius) {
        mColliderType = ColliderType.SPHERE;
        mGameObject = gameObject;
        mTag = tag;
        mOffset = offSet;
        mBoundingSphere = new BoundingSphere(gameObject.getPosition().cpy().add(offSet), radius);
    }

    public void updatePosition() {
        mBoundingSphere.setCenter(mGameObject.getPosition().cpy().add(mOffset));
    }

    public void setPosition(Vector3 newGameObjectPosition) {
        mBoundingSphere.setCenter(newGameObjectPosition.cpy().add(mOffset));
    }

    public void adjustPosition(Vector3 adjustment) {
        mBoundingSphere.setCenter(mBoundingSphere.getCenter().cpy().add(adjustment));
    }

    public void setRadius(float radius) {
        mBoundingSphere.setRadius(radius);
    }

    @Override
    public boolean intersectsWith(Collider other) {
        switch (other.mColliderType) {
            case BOX:
                return intersects(((BoxCollider) other).getBoundingBox(), mBoundingSphere);
            case SPHERE:
                return intersects(((SphereCollider) other).getBoundingSphere(), mBoundingSphere);
        }
        return false;
    }

    public BoundingSphere getBoundingSphere() {
        return mBoundingSphere;
    }
}
