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
 * This class extends the collider super class and implements the methods using a bounding sphere.
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

    /**
     * Updates the position of the bounding sphere based on the game objects position
     */
    public void updatePosition() {
        mBoundingSphere.setCenter(mGameObject.getPosition().cpy().add(mOffset));
    }

    /**
     * Sets the position of the bounding sphere
     *
     * @param newGameObjectPosition the new position
     */
    public void setPosition(Vector3 newGameObjectPosition) {
        mBoundingSphere.setCenter(newGameObjectPosition.cpy().add(mOffset));
    }

    /**
     * Adds the provided vector 3 to the colliders current position
     *
     * @param adjustment amound as a vector 3
     */
    public void adjustPosition(Vector3 adjustment) {
        mBoundingSphere.setCenter(mBoundingSphere.getCenter().cpy().add(adjustment));
    }

    public void setRadius(float radius) {
        mBoundingSphere.setRadius(radius);
    }

    @Override
    public boolean intersectsWith(Collider other) {
        //determine which method to run based on the colider type and then fill in the bounding shapes
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
