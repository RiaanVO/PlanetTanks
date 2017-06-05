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

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.riaanvo.planettanks.managers.ContentManager;
import com.riaanvo.planettanks.managers.GameObjectManager;

/**
 * This class manages the model instances of a tank. It updates their location, orientation and
 * aiming direction. It also provideds a method for the tank to shoot.
 */

public class TankController {

    private GameObject mParent;
    private ModelInstance mTankBase;
    private ModelInstance mTankTurret;

    private GameObjectManager mGameObjectManager;
    private CameraController mCameraController;

    private float mBodyOrientation;
    private float mTurretOrientation;

    private Vector3 mAimingDirection;

    //Used to set the starting position of a shell
    private float mBulletStartOffset;
    private float mBulletStartHeight;


    public TankController(Model tankBase, Model tankTurret, ColorAttribute tankColour) {
        mTankBase = new ModelInstance(tankBase);
        //Attempt to apply a colour to the tanks body if a colour is provided
        if (tankColour != null) mTankBase.materials.get(0).set(tankColour);
        mTankTurret = new ModelInstance(tankTurret);

        mCameraController = CameraController.get();
        mGameObjectManager = GameObjectManager.get();

        mBulletStartOffset = 1.5f;
        mBulletStartHeight = 0.8f;
    }

    /**
     * Fires a shell in the tanks current aiming direction.
     */
    public void shoot() {
        if (mAimingDirection == null) return;
        //Uses the parent gameobjects' position and offset values to calculate a starting position
        Vector3 startingPosition = mParent.getPosition().cpy();
        startingPosition.y = mBulletStartHeight;
        startingPosition.add(mAimingDirection.cpy().nor().scl(mBulletStartOffset));

        //Create a new shell and add it to the game object manager
        mGameObjectManager.addGameObject(new Shell(ContentManager.get().getShell(), startingPosition, mAimingDirection));
    }


    /**
     * Draws the tanks body and turret
     *
     * @param spriteBatch used to render 2D images
     * @param modelBatch used to render 3D models
     */
    public void renderTank(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        modelBatch.begin(mCameraController.getCamera());
        modelBatch.render(mTankBase, mCameraController.getEnvironment());
        modelBatch.render(mTankTurret, mCameraController.getEnvironment());
        modelBatch.end();
    }

    /**
     * Sets the orientation of the tanks body based on the direction passed in
     *
     * @param direction the tanks body will face
     */
    public void setBodyDirection(Vector3 direction) {
        //Calculates and applies the change in rotation
        float changeInOrientation = mParent.calculateOrientation(direction) - mBodyOrientation;
        mTankBase.transform.rotate(Vector3.Y, changeInOrientation);
        mBodyOrientation = mParent.calculateOrientation(direction);
    }

    /**
     * Sets the orientation of the tanks turret base on the direction passed in
     *
     * @param direction the tanks turret will face
     */
    public void setTurretDirection(Vector3 direction) {
        mAimingDirection = direction.cpy();
        float newOrientation = mParent.calculateOrientation(mAimingDirection);
        rotateTurret(newOrientation);
    }

    /**
     * Rotates the turret to the orientation provided
     *
     * @param orientation that the turret will be rotated to
     */
    private void rotateTurret(float orientation) {
        float changeInOrientation = orientation - mTurretOrientation;
        mTankTurret.transform.rotate(Vector3.Y, changeInOrientation);
        if (orientation > 360) {
            orientation -= 360;
        }
        if (orientation < 0) {
            orientation += 360;
        }
        mTurretOrientation = orientation;
    }

    /**
     * Sets the position of the models
     *
     * @param position that the tank model is located
     */
    public void setPosition(Vector3 position) {
        mTankBase.transform.setTranslation(position);
        mTankTurret.transform.setTranslation(position);
    }

    public void setParent(GameObject parent) {
        mParent = parent;
    }

    public float getTurretOrientation() {
        return mTurretOrientation;
    }

    /**
     * Sets the aiming direction and rotates the turret to face the orientation provided
     * @param orientation
     */
    public void setTurretOrientation(float orientation) {
        rotateTurret(orientation);
        mAimingDirection = mParent.calculateDirection(mTurretOrientation);
    }
}
