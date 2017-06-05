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

package com.riaanvo.planettanks.managers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.riaanvo.planettanks.GameObjects.CameraController;
import com.riaanvo.planettanks.Physics.BoxCollider;
import com.riaanvo.planettanks.Physics.Collider;
import com.riaanvo.planettanks.Physics.SphereCollider;

import java.util.LinkedList;

/**
 * This class manages all the colliders in the game that are used for game object interactions. It
 * provides the functionality to get a list of intersecting colliders. It is accessible anywhere in
 * the game through the use of the singleton pattern. It also can render the colliders that are
 * applied to the game objects
 */

public class CollisionManager {
    //Used for debugging the colliders
    private static final boolean DEBUG_COLLIDERS = false;
    private ShapeRenderer shapeRenderer;
    private LinkedList<Vector3> mSpherePoints;


    private static CollisionManager sCollisionManager;

    /**
     * Gets the instance of the collision manager. Creates one if there isn't an instance
     *
     * @return the instance of the collision manager
     */
    public static CollisionManager get() {
        if (sCollisionManager == null) sCollisionManager = new CollisionManager();
        return sCollisionManager;
    }

    //A store of all the colliders used for game object interaction
    private LinkedList<Collider> mColliders = new LinkedList<Collider>();

    private CollisionManager() {
        //Create a shape render if debugging is turned on
        if (DEBUG_COLLIDERS) {
            shapeRenderer = new ShapeRenderer();
        }
    }

    /**
     * Adds a collider to the list of colliders
     *
     * @param collider to be added
     */
    public void addCollider(Collider collider) {
        mColliders.add(collider);
    }

    /**
     * Removes a collider from the list of colliders
     *
     * @param collider to be removed
     */
    public void removeCollider(Collider collider) {
        mColliders.remove(collider);
    }

    /**
     * Clears the list of colliders
     */
    public void clearColliders() {
        mColliders.clear();
    }

    /**
     * Returns a list of colliders that intersected with the base collider and had the tag provided
     *
     * @param baseCollider the collider used to find intersecting colliders
     * @param tag          of colliders to test against
     * @return a list of colliders intersecting with the provided collider
     */
    public LinkedList<Collider> getCollisions(Collider baseCollider, Collider.ColliderTag tag) {
        LinkedList<Collider> collisions = new LinkedList<Collider>();
        for (Collider other : mColliders) {
            //Check if the collider has the tag provided or has the tag all and is not the passed in collider
            if ((other.hasTag(tag) || tag == Collider.ColliderTag.ALL) && baseCollider != other) {
                //Adds it to the list if it intersects
                if (baseCollider.intersectsWith(other)) collisions.add(other);
            }
        }
        return collisions;
    }

    /**
     * Checks if the collider intersects with any of the colliders provided
     *
     * @param baseCollider collider to check intersection with
     * @param others       that the intersection check will be tested on
     * @return if the collider intersects with any of the provided colliders
     */
    public boolean getCollisionsInListBoolean(Collider baseCollider, LinkedList<Collider> others) {
        for (Collider other : others) {
            if (baseCollider.intersectsWith(other)) return true;
        }
        return false;
    }

    /**
     * Draws the debug collider shapes in the scene if debugging is on
     *
     * @param collider that will be drawn
     */
    public void renderCollider(Collider collider) {
        if (!DEBUG_COLLIDERS) return;
        //Set up the view that the shape will be rendered into
        shapeRenderer.setProjectionMatrix(CameraController.get().getCamera().combined);

        if (collider.isType(Collider.ColliderType.BOX)) {
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            //Get the dimensions of the box collider
            Vector3 minPoint = ((BoxCollider) collider).getMinBound();
            Vector3 size = ((BoxCollider) collider).getSize();

            //Render a box in the scene using the gathered dimensions
            shapeRenderer.box(minPoint.x, minPoint.y, minPoint.z, size.x, size.y, -size.z);
            shapeRenderer.end();

        } else if (collider.isType(Collider.ColliderType.SPHERE)) {
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Point);

            //Get the dimenstions of the sphere collider
            Vector3 center = ((SphereCollider) collider).getBoundingSphere().getCenter();
            float radius = ((SphereCollider) collider).getBoundingSphere().getRadius();

            //For each point in the sphere, set is position and render it to the dimensions gathered
            for (Vector3 point : getSpherePoints()) {
                Vector3 newPoint = point.cpy();
                newPoint.scl(radius);
                newPoint.add(center);
                shapeRenderer.point(newPoint.x, newPoint.y, newPoint.z);
            }
            shapeRenderer.end();
        }

    }

    /**
     * Creates and returns a list of points that lie on the surface of a sphere
     * Code adapted from https://stackoverflow.com/questions/9600801/evenly-distributing-n-points-on-a-sphere
     *
     * @return the list of points that make up a sphere
     */
    private LinkedList<Vector3> getSpherePoints() {
        if (mSpherePoints != null) return mSpherePoints;
        mSpherePoints = new LinkedList<Vector3>();

        //Set the number of points to be created and the variables used to create them
        int samples = 1000;
        double rnd = 10d;
        double offset = 2d / samples;
        double increment = Math.PI * (3 - Math.sqrt(5));

        //Loop through and created all the positions
        for (int i = 0; i < samples; i++) {
            double y = ((i * offset) - 1) + (offset / 2);
            double r = Math.sqrt(1 - Math.pow(y, 2));
            double phi = ((i + rnd) % samples) * increment;
            double x = Math.cos(phi) * r;
            double z = Math.sin(phi) * r;
            mSpherePoints.add(new Vector3((float) x, (float) y, (float) z));
        }
        return mSpherePoints;
    }

    /**
     * Dispose of the shape render if it has been created
     */
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
            shapeRenderer = null;
        }
    }
}
