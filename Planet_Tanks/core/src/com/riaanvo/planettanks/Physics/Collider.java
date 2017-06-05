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
 * This class is the collider super class that is extended into the two types, box collider and
 * sphere collider. It provides the methods to check for intersection against the different types
 * of colliders.
 */

public abstract class Collider {
    public enum ColliderTag {
        ALL,
        WALL,
        ENTITIES,
        PROJECTILES,
        TRAPS
    }

    public enum ColliderType {
        SPHERE,
        BOX
    }

    protected ColliderType mColliderType;
    protected GameObject mGameObject;
    protected ColliderTag mTag;
    protected Vector3 mOffset;

    /**
     * Used to test if this collider is intersecting with the provided collider
     *
     * @param other the other collider to be tested against
     * @return if the collider is intersecting with the provided collider
     */
    public abstract boolean intersectsWith(Collider other);

    /**
     * Checks if the provided tag matches this colliders tag
     *
     * @param testTag tag to be tested for
     * @return if the test tag is the same as this colliders tag
     */
    public boolean hasTag(ColliderTag testTag) {
        return mTag == testTag;
    }

    public void setOffset(Vector3 newOffset) {
        mOffset = newOffset;
    }

    /**
     * Checks if the first bounding box intersects the second bounding box
     *
     * @param box1 the first bounding box
     * @param box2 the second bounding box
     * @return if the bounding boxes intersect
     */
    protected boolean intersects(BoundingBox box1, BoundingBox box2) {
        return box1.intersects(box2);
    }

    /**
     * Checks if the bounding box intersects with the bounding sphere
     *
     * @param box    the bounding box
     * @param sphere the bounding sphere
     * @return
     */
    protected boolean intersects(BoundingBox box, BoundingSphere sphere) {
        return sphere.intersects(box);
    }

    /**
     * Checks if the first bounding sphere intersects with the second bounding sphere
     *
     * @param sphere1 the first bounding sphere
     * @param sphere2 the second bounding sphere
     * @return if the first sphere intersect the second sphere
     */
    protected boolean intersects(BoundingSphere sphere1, BoundingSphere sphere2) {
        return sphere1.intersects(sphere2);
    }

    public boolean isType(ColliderType type) {
        return mColliderType == type;
    }

    public GameObject getGameObject() {
        return mGameObject;
    }

}
