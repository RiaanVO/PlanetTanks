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
import com.badlogic.gdx.math.Vector3;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.Physics.Collider;
import com.riaanvo.planettanks.Physics.SphereCollider;
import com.riaanvo.planettanks.managers.CollisionManager;
import com.riaanvo.planettanks.managers.GameObjectManager;

import java.util.LinkedList;

/**
 * This class creates and manages an individual shell. Handles dealing damage to other living game
 * objects if a collision is detected
 */

public class Shell extends LivingGameObject {
    private ModelInstance mShell;
    private CameraController mCameraController;

    //Colliders used to check movement and collision with other living game objects
    private CollisionManager mCollisionManager;
    private SphereCollider mBaseSphereCollider;
    private SphereCollider mXTestCollider;
    private SphereCollider mZTestCollider;

    private Vector3 mMoveDirection;
    private float mMoveSpeed;

    //Used to make the bullets interaction with the scene
    private final int mMaxBounceCount;
    private int mCurrentBounceCount;
    private int mHitDamage;

    public Shell(Model shell, Vector3 startingPosition, Vector3 startingDirection) {
        super();
        setTag(Constants.TAG_BASIC_SHELL);
        setHealth(1);

        mShell = new ModelInstance(shell);
        mCameraController = CameraController.get();
        //Set the direction of travel
        mMoveDirection = startingDirection.cpy();

        //Set up the colliders used for controlling the shells motion and collision checks
        float colliderRadius = 0.1f;
        Vector3 colliderOffset = new Vector3(0, 0, 0);
        mCollisionManager = CollisionManager.get();
        mBaseSphereCollider = new SphereCollider(this, Collider.ColliderTag.PROJECTILES, colliderOffset, colliderRadius);
        mCollisionManager.addCollider(mBaseSphereCollider);
        mXTestCollider = new SphereCollider(this, Collider.ColliderTag.PROJECTILES, colliderOffset, colliderRadius);
        mZTestCollider = new SphereCollider(this, Collider.ColliderTag.PROJECTILES, colliderOffset, colliderRadius);

        setPosition(startingPosition);

        //Set up base shell variables
        mMaxBounceCount = 1;
        mCurrentBounceCount = 0;
        mMoveSpeed = 3.5f;
        mHitDamage = 1;
    }

    @Override
    public void update(float deltaTime) {
        if (isDead()) {
            handelDeath();
            return;
        }

        //Attempt to move the shell
        move(mMoveDirection, deltaTime);
        //Check if the shell has collided with either entities or other projectiles
        checkDamageCollisions(Collider.ColliderTag.ENTITIES);
        checkDamageCollisions(Collider.ColliderTag.PROJECTILES);

        //Destroy the bullet if it has passed its bounce limit
        if (mCurrentBounceCount > mMaxBounceCount) {
            takeDamage(getHealth());
        }
    }

    /**
     * Attempt to move the shell in the direction of travel.
     *
     * @param direction that the shell will travel in
     * @param deltaTime the time since last update
     */
    private void move(Vector3 direction, float deltaTime) {
        //Setup the new position of the shell
        direction.nor();
        Vector3 moveAdjustment = direction.cpy().scl(deltaTime * mMoveSpeed);
        Vector3 newPosition = moveAdjustment.cpy().add(getPosition());
        mBaseSphereCollider.setPosition(newPosition);

        //Check collisions with the walls and store the list
        LinkedList<Collider> collisions = mCollisionManager.getCollisions(mBaseSphereCollider, Collider.ColliderTag.WALL);
        if (collisions.size() > 0) {
            //Set up the components of movement and apply it to the test colliders
            Vector3 xMoveAdjustment = moveAdjustment.cpy();
            xMoveAdjustment.z = 0;
            Vector3 zMoveAdjustment = moveAdjustment.cpy();
            zMoveAdjustment.x = 0;

            mXTestCollider.adjustPosition(xMoveAdjustment);
            mZTestCollider.adjustPosition(zMoveAdjustment);

            //If a component hits flip its movement direction, calculate it new position and increase its bounce count
            if (mCollisionManager.getCollisionsInListBoolean(mXTestCollider, collisions)) {
                newPosition.x = getPosition().x - xMoveAdjustment.x;
                mMoveDirection.x = -mMoveDirection.x;
                mCurrentBounceCount++;
            }
            if (mCollisionManager.getCollisionsInListBoolean(mZTestCollider, collisions)) {
                newPosition.z = getPosition().z - zMoveAdjustment.z;
                mMoveDirection.z = -mMoveDirection.z;
                mCurrentBounceCount++;
            }
        }

        //Don't move if the bounce count is larger than the max number of bounces
        if (mCurrentBounceCount > mMaxBounceCount) return;

        setPosition(newPosition);
        rotateShell(direction);
    }

    /**
     * Checks to see if the base collider has hit any other colliders of the tag type and attempt
     * to damage them.
     *
     * @param tag of the colliders to check collision with
     */
    private void checkDamageCollisions(Collider.ColliderTag tag) {
        LinkedList<Collider> collisions = mCollisionManager.getCollisions(mBaseSphereCollider, tag);
        if (collisions.size() > 0) {
            //attempt to damage the other colliders and kill itself
            damageOtherLivingGameObjects(collisions);
            takeDamage(getHealth());
        }
    }

    /**
     * Check if any collider in the provided list is a living game object and then damage it
     *
     * @param others a list of colliders
     */
    private void damageOtherLivingGameObjects(LinkedList<Collider> others) {
        for (Collider collider : others) {
            LivingGameObject livingGameObject = ((LivingGameObject) collider.getGameObject());
            if (livingGameObject != null) {
                livingGameObject.takeDamage(mHitDamage);
            }
        }
    }

    /**
     * Rotates the shell to face the direction of travel
     *
     * @param direction of travel
     */
    private void rotateShell(Vector3 direction) {
        float newOrientation = calculateOrientation(direction);
        float changeInOrientation = newOrientation - mOrientation;
        mShell.transform.rotate(Vector3.Y, changeInOrientation);
        mOrientation = newOrientation;
    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        modelBatch.begin(mCameraController.getCamera());
        modelBatch.render(mShell);
        modelBatch.end();

        //Attempt to render the shell collider
        mCollisionManager.renderCollider(mBaseSphereCollider);
    }

    @Override
    public void setPosition(Vector3 position) {
        mPosition = position.cpy();
        mShell.transform.setTranslation(mPosition);
        mBaseSphereCollider.updatePosition();
        mXTestCollider.updatePosition();
        mZTestCollider.updatePosition();
    }

    @Override
    protected void handelDeath() {
        if (!mDeathHandled) {
            //Remove the shell from the collision and game object manager
            mCollisionManager.removeCollider(mBaseSphereCollider);
            GameObjectManager.get().removeGameObject(this);
            mDeathHandled = true;
        }
    }
}
