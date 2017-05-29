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
    private static final boolean debugColliders = true;

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
        LinkedList<Collider> collsions = new LinkedList<Collider>();
        for (Collider other : mColliders) {
            if ((other.hasTag(testTag) || testTag == Collider.ColliderTag.ALL) && baseCollider != other) {
                if (baseCollider.intersectsWith(other)) collsions.add(other);
            }
        }
        return collsions;
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
        shapeRenderer.dispose();
    }
}
