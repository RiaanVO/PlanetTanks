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
 * Created by riaanvo on 22/5/17.
 */

public class CollisionManager {
    private static final boolean debugColliders = false;

    private static CollisionManager sCollisionManager;

    public static CollisionManager get() {
        if (sCollisionManager == null) sCollisionManager = new CollisionManager();
        return sCollisionManager;
    }

    private LinkedList<Collider> mColliders = new LinkedList<Collider>();

    //Debugging
    ShapeRenderer shapeRenderer;

    private CollisionManager() {
        shapeRenderer = new ShapeRenderer();
    }

    public void addCollider(Collider collider) {
        mColliders.add(collider);
    }

    public void removeCollider(Collider collider) {
        mColliders.remove(collider);
    }

    public void clearColliders() {
        mColliders.clear();
    }

    public LinkedList<Collider> getCollisions(Collider baseCollider, Collider.ColliderTag testTag) {
        LinkedList<Collider> collisions = new LinkedList<Collider>();
        for (Collider other : mColliders) {
            if ((other.hasTag(testTag) || testTag == Collider.ColliderTag.ALL) && baseCollider != other) {
                if (baseCollider.intersectsWith(other)) collisions.add(other);
            }
        }
        return collisions;
    }

    public boolean getCollisionsInListBoolean(Collider baseCollider, LinkedList<Collider> others) {
        for (Collider other : others) {
            if (baseCollider.intersectsWith(other)) return true;
        }
        return false;
    }

    public void renderCollider(Collider collider) {
        if (!debugColliders) return;
        shapeRenderer.setProjectionMatrix(CameraController.get().getCamera().combined);

        if (collider.isType(Collider.ColliderType.BOX)) {
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            Vector3 minPoint = ((BoxCollider) collider).getMinBound();
            Vector3 size = ((BoxCollider) collider).getSize();

            shapeRenderer.box(minPoint.x, minPoint.y, minPoint.z, size.x, size.y, -size.z);
            shapeRenderer.end();

        } else if (collider.isType(Collider.ColliderType.SPHERE)) {

            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Point);
            Vector3 center = ((SphereCollider) collider).getBoundingSphere().getCenter();
            float radius = ((SphereCollider) collider).getBoundingSphere().getRadius();

            for (Vector3 point : getSpherePoints()) {
                Vector3 newPoint = point.cpy();
                newPoint.scl(radius);
                newPoint.add(center);
                shapeRenderer.point(newPoint.x, newPoint.y, newPoint.z);
            }
            shapeRenderer.end();
        }

    }

    private LinkedList<Vector3> spherePoints;

    private LinkedList<Vector3> getSpherePoints() {
        //https://stackoverflow.com/questions/9600801/evenly-distributing-n-points-on-a-sphere
        if (spherePoints != null) return spherePoints;
        spherePoints = new LinkedList<Vector3>();

        int samples = 1000;
        double rnd = 10d;
        double offset = 2d / samples;
        double increment = Math.PI * (3 - Math.sqrt(5));

        for (int i = 0; i < samples; i++) {
            double y = ((i * offset) - 1) + (offset / 2);
            double r = Math.sqrt(1 - Math.pow(y, 2));
            double phi = ((i + rnd) % samples) * increment;
            double x = Math.cos(phi) * r;
            double z = Math.sin(phi) * r;
            spherePoints.add(new Vector3((float) x, (float) y, (float) z));
        }
        return spherePoints;
    }

    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
            shapeRenderer = null;
        }
    }
}
