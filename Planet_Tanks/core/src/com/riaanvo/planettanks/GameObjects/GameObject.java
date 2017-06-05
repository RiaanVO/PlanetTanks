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

package com.riaanvo.planettanks.GameObjects;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.riaanvo.planettanks.Constants;

/**
 * The superclass to all in game objects. Defines its position, orientation and tag.
 */

public abstract class GameObject {
    protected String mTag;
    protected Vector3 mPosition;
    protected float mOrientation;

    public GameObject() {
        this(Vector3.Zero);
    }

    public GameObject(Vector3 position) {
        this(position, 0f);
    }

    public GameObject(Vector3 position, float orientation) {
        mPosition = position;
        mOrientation = orientation;
        //Apply a default tag unless assigned one
        mTag = Constants.TAG_DEFAULT;
    }

    /**
     * The update method that will be called on each game object to update its logic
     *
     * @param deltaTime the change in time since the last update
     */
    public abstract void update(float deltaTime);

    /**
     * The drawing method that will be called on each game object to draw it in the scene
     *
     * @param spriteBatch used for drawing 2D images
     * @param modelBatch  used for rendering 3D models
     */
    public abstract void render(SpriteBatch spriteBatch, ModelBatch modelBatch);

    /**
     * Checks if the game object is in the cameras' view
     * Code adapted from https://xoppa.github.io/blog/3d-frustum-culling-with-libgdx/
     *
     * @return if the object is visible to the camera
     */
    public boolean isVisible() {
        Camera cam = CameraController.get().getCamera();
        return cam.frustum.boundsInFrustum(mPosition, Constants.RENDER_BOUNDS);
    }

    /**
     * Calculates the angle of orientation from a vector using its x and z components
     *
     * @param direction vector that will be converted into an orientation.
     * @return the angle of orientation
     */
    protected float calculateOrientation(Vector3 direction) {
        float newOrientation;
        if (direction.x != 0) {
            if (direction.x < 0) {
                newOrientation = 360 - (float) Math.toDegrees(Math.atan2(direction.x, direction.z)) * -1;
            } else {
                newOrientation = (float) Math.toDegrees(Math.atan2(direction.x, direction.z));
            }
        } else {
            if (direction.z > 0) {
                newOrientation = 0;
            } else {
                newOrientation = 180;
            }
        }
        return newOrientation;
    }

    /**
     * Calculates a vector 3 that points in the direction of the provided orientation
     *
     * @param orientation the angle to be converted into a vector
     * @return a vector3 in the xz plane that points in the direction of the orientation
     */
    protected Vector3 calculateDirection(float orientation) {
        float castOrientation = Math.abs(orientation);
        boolean negX = false;
        boolean negZ = false;
        boolean normalCalc = true;
        float x;
        float z;

        //Determine quadrant of unit circle the angle is in and then alter it to the first quadrant
        if (castOrientation > 270) {
            castOrientation = 360 - castOrientation;
            negX = true;
        } else if (castOrientation > 180) {
            castOrientation = 270 - castOrientation;
            negZ = true;
            negX = true;
            normalCalc = false;
        } else if (castOrientation > 90) {
            castOrientation = 180 - castOrientation;
            negZ = true;
        }

        //Calculate the components of the triangle
        if (normalCalc) {
            x = (float) Math.sin(Math.toRadians(castOrientation));
            z = (float) Math.cos(Math.toRadians(castOrientation));
        } else {
            x = (float) Math.cos(Math.toRadians(castOrientation));
            z = (float) Math.sin(Math.toRadians(castOrientation));
        }

        //Apply the signs to the lengths
        if (negX) x *= -1;
        if (negZ) z *= -1;

        return new Vector3(x, 0, z);
    }

    protected void setTag(String newTag) {
        mTag = newTag;
    }

    public String getTag() {
        return mTag;
    }

    public boolean compareTag(String testTag) {
        return mTag.equals(testTag);
    }

    public Vector3 getPosition() {
        return mPosition;
    }

    public void setPosition(Vector3 position) {
        mPosition = position;
    }

    public float getOrientation() {
        return mOrientation;
    }

    public void setOrientation(float orientation) {
        mOrientation = orientation;
    }

}
