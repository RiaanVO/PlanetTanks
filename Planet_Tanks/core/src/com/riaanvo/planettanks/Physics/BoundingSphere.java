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

/**
 * This class creates a bounding sphere and has the functions to test intersects
 * with a bounding box and other bounding spheres
 */

public class BoundingSphere {
    private Vector3 mCenter;
    private float mRadius;
    private float mRadius2;

    public BoundingSphere(Vector3 center, float radius) {
        mCenter = center;
        mRadius = radius;
        mRadius2 = radius * radius;
    }

    /**
     * Checks if the provided bounding sphere intersects with this bounding sphere
     *
     * @param other the other bounding sphere
     * @return if the bounding sphere intersects this sphere
     */
    public boolean intersects(BoundingSphere other) {
        Vector3 otherPosition = other.getCenter();
        float distanceBetweenSpheres = Vector3.dst2(mCenter.x, mCenter.y, mCenter.z, otherPosition.x, otherPosition.y, otherPosition.z);
        float actualDistance2 = (mRadius + other.getRadius()) * (mRadius + other.getRadius());
        return distanceBetweenSpheres <= actualDistance2;
    }

    /**
     * Checks if the provided bounding box intersect this bounding sphere
     * Code adapted from http://stackoverflow.com/questions/15247347/collision-detection-between-a-boundingbox-and-a-sphere-in-libgdx
     *
     * @param other the bounding box to test against
     * @return if the bounding box and sphere intersect
     */
    public boolean intersects(BoundingBox other) {
        if (roughSphereIntersectsCheck(other)) {
            float dmin = 0;
            Vector3 center = mCenter;
            Vector3 bmin = new Vector3();
            other.getMin(bmin);
            Vector3 bmax = new Vector3();
            other.getMax(bmax);

            if (center.x < bmin.x) {
                dmin += Math.pow(center.x - bmin.x, 2);
            } else if (center.x > bmax.x) {
                dmin += Math.pow(center.x - bmax.x, 2);
            }

            if (center.y < bmin.y) {
                dmin += Math.pow(center.y - bmin.y, 2);
            } else if (center.y > bmax.y) {
                dmin += Math.pow(center.y - bmax.y, 2);
            }

            if (center.z < bmin.z) {
                dmin += Math.pow(center.z - bmin.z, 2);
            } else if (center.z > bmax.z) {
                dmin += Math.pow(center.z - bmax.z, 2);
            }

            return dmin <= Math.pow(mRadius, 2);
        } else {
            return false;
        }
    }

    /**
     * Checks if the provided bounding box collides with this sphere collider using a rough
     * bounding sphere. Reduces the number of checks when testing against a bounding box
     *
     * @param other the bounding box to test against intersection
     * @return if this sphere intersects with the rough bounding sphere
     */
    private boolean roughSphereIntersectsCheck(BoundingBox other) {
        float otherRadius2 = (float) (Math.pow(other.getWidth() / 2, 2) + Math.pow(other.getHeight() / 2, 2) + Math.pow(other.getDepth() / 2, 2));
        float minNoCollidingDistance = (float) Math.pow(mRadius2 + otherRadius2, 2);
        float actualDistance2 = Vector3.dst2(mCenter.x, mCenter.y, mCenter.z, other.getCenterX(), other.getCenterY(), other.getCenterZ());
        return actualDistance2 <= minNoCollidingDistance;
    }

    public void setCenter(Vector3 newCenter) {
        mCenter = newCenter;
    }

    public Vector3 getCenter() {
        return mCenter;
    }

    /**
     * Sets the radius and calculates and sets the radius squared
     *
     * @param newRadius the new radius to be used
     */
    public void setRadius(float newRadius) {
        mRadius = newRadius;
        mRadius2 = mRadius * mRadius;
    }

    public float getRadius() {
        return mRadius;
    }
}
