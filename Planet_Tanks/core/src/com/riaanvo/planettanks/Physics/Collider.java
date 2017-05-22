package com.riaanvo.planettanks.Physics;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Sphere;
import com.riaanvo.planettanks.Objects.GameObject;

/**
 * Created by riaanvo on 22/5/17.
 */

public abstract class Collider {
    public enum  ColliderTag {
        WALLS,
        ENTITIES,
        PROJECTILES
    }

    protected GameObject mGameObject;
    protected ColliderTag mTag;

    public abstract boolean isColliding(BoundingBox other);
    public abstract boolean isColliding(Sphere other);

    public boolean isTag(ColliderTag testTag){
        return mTag == testTag;
    }

}
