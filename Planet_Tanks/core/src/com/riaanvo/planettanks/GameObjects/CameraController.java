package com.riaanvo.planettanks.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by riaanvo on 12/5/17.
 */

public class CameraController {
    private Camera mCamera;
    private Environment mEnvironment;

    private GameObject trackingObject;
    private Vector3 trackingPoint;
    private Vector3 cameraPositionOffset;
    private float mLerp = 0.05f;

    private static CameraController sCameraController;

    public static CameraController get() {
        if (sCameraController == null) {
            sCameraController = new CameraController();
        }
        return sCameraController;
    }

    private CameraController() {
        mEnvironment = new Environment();
        mEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        mEnvironment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    public void CreatePerspective(float fieldOfViewY, float viewportWidth, float viewportHeight, float near, float far, Vector3 lookAtPoint, Vector3 cameraOffset) {
        mCamera = new PerspectiveCamera(fieldOfViewY, viewportWidth, viewportHeight);
        mCamera.near = near;
        mCamera.far = far;

        cameraPositionOffset = cameraOffset;
        mCamera.position.set(cameraOffset.cpy().sub(lookAtPoint));
        mCamera.lookAt(lookAtPoint);
        trackingPoint = lookAtPoint;
        mCamera.update();
    }

    public void update(float deltaTime) {
        if (trackingObject == null) return;
        Vector3 newPosition = trackingObject.getPosition().cpy();
        newPosition.add(cameraPositionOffset);
        moveCamera(mCamera.position.cpy().lerp(newPosition, mLerp));
    }

    private void moveCamera(Vector3 newPosition) {
        mCamera.position.set(newPosition);
        mCamera.update();
    }

    public void setTrackingPoint(Vector3 position) {
        trackingPoint = position;
        mCamera.position.set(trackingPoint.cpy().add(cameraPositionOffset));
        mCamera.lookAt(trackingPoint);
        mCamera.update();
    }

    public void setCameraOffset(Vector3 offset) {
        cameraPositionOffset = offset;
        if (trackingObject == null) return;
        mCamera.position.set(trackingPoint.cpy().add(cameraPositionOffset));
        mCamera.lookAt(trackingPoint);
        mCamera.update();
    }


    public void setTrackingObject(GameObject gameObject) {
        trackingObject = gameObject;
        trackingPoint = gameObject.getPosition();

        Vector3 newPosition = trackingObject.getPosition().cpy();
        newPosition.add(cameraPositionOffset);
        moveCamera(newPosition);
    }

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
