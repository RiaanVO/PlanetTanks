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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;

/**
 * This class controls the games perspective camera. Uses the singleton pattern to allow all game
 * objects access for rendering. Updates the cameras position based on the game object it is tracking.
 */

public class CameraController {
    private Camera mCamera;
    private Environment mEnvironment;

    //used for tracking a gameobject
    private GameObject mTrackingObject;
    private Vector3 trackingPoint;
    private Vector3 cameraPositionOffset;
    private float mLerp;

    //Used to allow for access anywhere in the engine
    private static CameraController sCameraController;

    public static CameraController get() {
        if (sCameraController == null) {
            sCameraController = new CameraController();
        }
        return sCameraController;
    }

    private CameraController() {
        //Create a basic environment with a directional light used for rendering 3d models
        mEnvironment = new Environment();
        mEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        mEnvironment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        //Set the speed of the camera
        mLerp = 0.05f;
    }

    /**
     * Creates a new perspective camera that is used to render 3D objects
     *
     * @param fieldOfView    angle of the field of view
     * @param viewportWidth  the width of the cameras' view
     * @param viewportHeight the height of the cameras' view
     * @param near           minimum distance of closest object that can be rendered
     * @param far            maximum distance of an object that can be rendered
     * @param lookAtPoint    target position that the camera will face
     * @param cameraOffset   offset of the camera from the target position
     */
    public void CreatePerspective(float fieldOfView, float viewportWidth, float viewportHeight, float near, float far, Vector3 lookAtPoint, Vector3 cameraOffset) {
        mCamera = new PerspectiveCamera(fieldOfView, viewportWidth, viewportHeight);
        mCamera.near = near;
        mCamera.far = far;

        //calculate the cameras position from its target location and offset
        cameraPositionOffset = cameraOffset;
        mCamera.position.set(cameraOffset.cpy().sub(lookAtPoint));
        mCamera.lookAt(lookAtPoint);
        trackingPoint = lookAtPoint;
        mCamera.update();
    }

    /**
     * Update the cameras position based on the tracking objects current position
     *
     * @param deltaTime the time since last update
     */
    public void update(float deltaTime) {
        if (mTrackingObject == null) return;
        Vector3 newPosition = mTrackingObject.getPosition().cpy();
        newPosition.add(cameraPositionOffset);
        moveCamera(mCamera.position.cpy().lerp(newPosition, mLerp));
    }

    /**
     * Move the camera to the new position and update its view
     *
     * @param newPosition that the camera will be set to
     */
    private void moveCamera(Vector3 newPosition) {
        mCamera.position.set(newPosition);
        mCamera.update();
    }

    /**
     * Sets the position and direction the camera will face and look at
     *
     * @param position of the new look at point of the camera
     */
    public void setTrackingPoint(Vector3 position) {
        trackingPoint = position;
        mCamera.position.set(trackingPoint.cpy().add(cameraPositionOffset));
        mCamera.lookAt(trackingPoint);
        mCamera.update();
    }

    /**
     * Set the cameras's offset and update its position if there is a stored tracking object
     *
     * @param offset of the camera from the tracking location
     */
    public void setCameraOffset(Vector3 offset) {
        cameraPositionOffset = offset;
        if (mTrackingObject == null) return;
        mCamera.position.set(trackingPoint.cpy().add(cameraPositionOffset));
        mCamera.lookAt(trackingPoint);
        mCamera.update();
    }

    /**
     * Sets the gameobject that the camera will follow and updates its position to start at the correct
     * following position.
     *
     * @param gameObject that the camera will follow and look at
     */
    public void setTrackingObject(GameObject gameObject) {
        mTrackingObject = gameObject;
        trackingPoint = gameObject.getPosition();

        Vector3 newPosition = mTrackingObject.getPosition().cpy();
        newPosition.add(cameraPositionOffset);
        moveCamera(newPosition);
    }

    /**
     * Checks if the camera has been set up, and creates one if there is no camera
     *
     * @return a perspective camera
     */
    public Camera getCamera() {
        if (mCamera == null)
            CreatePerspective(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1f, 300f, new Vector3(0, 0, 0), new Vector3(0, 20, 10));
        return mCamera;
    }

    public Environment getEnvironment() {
        return mEnvironment;
    }

    public void setEnvironment(Environment newEnvironment) {
        mEnvironment = newEnvironment;
    }
}
