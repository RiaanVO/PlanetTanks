package com.riaanvo.planettanks.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by riaanvo on 12/5/17.
 */

public class CameraController {
    private Camera mCamera;

    private GameObject trackingObject;
    private Vector3 cameraPositionOffset;
    private float mLerp = 0.05f;

    private static CameraController sCameraController;
    public static CameraController get(){
        if(sCameraController == null) {
            sCameraController = new CameraController();
        }
        return sCameraController;
    }

    public void update(float deltaTime){
        if(trackingObject == null) return;


        //setLookAtAndPosition(trackingObject.getPosition(), mCamera.position);

//        System.out.println("Updating camera");
        Vector3 newPosition = trackingObject.getPosition().cpy();
        newPosition.add(cameraPositionOffset);
        //newPosition = Vector3Lerp(mCamera.position, newPosition, mLerp, deltaTime);
        setPosition(mCamera.position.cpy().lerp(newPosition, mLerp));
        //setPosition(newPosition);
    }

    private Vector3 Vector3Lerp(Vector3 startingVec, Vector3 endingVec, float lerp, float deltaTime){
        float x = (endingVec.x - startingVec.x) * lerp;// * deltaTime;
        float y = (endingVec.x - startingVec.x) * lerp;// * deltaTime;
        float z = (endingVec.x - startingVec.x) * lerp;// * deltaTime;
        return new Vector3(x, y, z);
    }

    public void CreatePerspective(){
        CreatePerspective(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1f, 300f);
    }

    public void CreatePerspective(float fieldOfViewY, float viewportWidth, float viewportHeight, float near, float far){
        mCamera = new PerspectiveCamera(fieldOfViewY, viewportWidth, viewportHeight);
        mCamera.near = near;
        mCamera.far = far;

        //Default to this position
        Vector3 startingPosition = new Vector3(0f, 15f, 5f);
        mCamera.position.set(startingPosition);
        mCamera.lookAt(0,0,0);
        cameraPositionOffset = startingPosition;

        mCamera.update();
    }

    public void setPosition(Vector3 position){
        if(mCamera == null) return;
        mCamera.position.set(position);
        mCamera.update();
    }

    public void setLookAtAndPosition(Vector3 lookAt, Vector3 position){
        if(mCamera == null) return;
        mCamera.lookAt(lookAt);
        mCamera.position.set(position);
        mCamera.update();
    }

    public void setTrackingObject(GameObject gameObject){
        trackingObject = gameObject;
    }

    public void setCameraPositionOffset(Vector3 offset){
        cameraPositionOffset = offset;
    }

    public Camera getCamera(){
        if(mCamera == null) CreatePerspective();
        return  mCamera;
    }
}
