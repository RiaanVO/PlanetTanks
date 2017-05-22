package com.riaanvo.planettanks.Physics;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Sphere;

/**
 * Created by riaanvo on 22/5/17.
 */

public class BoxCollider extends Collider {
    @Override
    public boolean isColliding(BoundingBox other) {
        return false;
    }

    @Override
    public boolean isColliding(Sphere other) {
        return false;
    }
}
