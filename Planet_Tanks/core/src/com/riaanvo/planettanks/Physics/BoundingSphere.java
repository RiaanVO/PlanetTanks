package com.riaanvo.planettanks.Physics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * Created by riaanvo on 22/5/17.
 */

public class BoundingSphere {
    private Vector3 mCenter;
    private float mRadius;

    public BoundingSphere(Vector3 center, float radius){
        mCenter = center;
        mRadius = radius;
    }

    public boolean intersects(BoundingSphere other){
        Vector3 otherPosition = other.getCenter();
        float distanceBetweenSpheres = Vector3.dst2(mCenter.x, mCenter.y, mCenter.z, otherPosition.x, otherPosition.y, otherPosition.z);
        float actualDistance2 = (mRadius + other.getRadius()) * (mRadius + other.getRadius());
        return distanceBetweenSpheres <= actualDistance2;
    }

    //http://stackoverflow.com/questions/15247347/collision-detection-between-a-boundingbox-and-a-sphere-in-libgdx
    public boolean intersects(BoundingBox other){
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
    }

    public void setCenter(Vector3 newCenter){
        mCenter = newCenter;
    }

    public Vector3 getCenter(){
        return mCenter;
    }

    public void setRadius(float newRadius){
        mRadius = newRadius;
    }

    public float getRadius(){
        return mRadius;
    }
}
