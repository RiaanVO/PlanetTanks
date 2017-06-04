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

    public abstract boolean intersectsWith(Collider other);

    public boolean hasTag(ColliderTag testTag) {
        return mTag == testTag;
    }

    public void setOffset(Vector3 newOffset) {
        mOffset = newOffset;
    }

    protected boolean intersects(BoundingBox box1, BoundingBox box2) {
        return box1.intersects(box2);
    }

    protected boolean intersects(BoundingBox box, BoundingSphere sphere) {
        return sphere.intersects(box);
    }

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
