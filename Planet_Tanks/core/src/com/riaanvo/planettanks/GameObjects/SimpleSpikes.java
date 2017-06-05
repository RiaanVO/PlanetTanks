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
import com.riaanvo.planettanks.Physics.BoxCollider;
import com.riaanvo.planettanks.Physics.Collider;
import com.riaanvo.planettanks.managers.CollisionManager;

import java.util.LinkedList;

/**
 * This class creates and controls a spike trap game object. The spikes move in a cyclic pattern and
 * attempt to damage any entity that collides with them.
 */

public class SimpleSpikes extends GameObject {
    private ModelInstance mSpikesSpikes;
    private ModelInstance mSpikesBase;

    private CameraController mCameraController;
    private CollisionManager mCollisionManager;

    //Collider used to check for damage collisions
    private BoxCollider mBoxCollider;

    //Varibles used to control the motion of the spikes
    private float mTransitionAngle;
    private float mCycleSpeed;
    private float mMinWaitTime;
    private float mWaitTimer;
    private Vector3 mSpikesDownOffset;
    private Vector3 mSpikesPosition;
    private Vector3 mStartingPosition;
    private boolean mWaiting;

    //Used to control the amount of damage and when the spikes can damage
    private float mSpikeAngleDamageThreshold;
    private int mHitDamage;

    //Used to reduce the number of collision checks
    private int mColliderCheckMaxDelay;
    private int mColliderCheckDelay;

    public SimpleSpikes(Model spikesBase, Model spikesSpikes, Vector3 position) {
        mSpikesBase = new ModelInstance(spikesBase);
        mSpikesSpikes = new ModelInstance(spikesSpikes);

        mCameraController = CameraController.get();
        mCollisionManager = CollisionManager.get();

        //Move the spikes base and then set the position of the spikes
        mSpikesBase.transform.setTranslation(position);
        setPosition(position);

        //Setup the spikes collider
        float colliderShrink = 0.7f;
        mBoxCollider = new BoxCollider(this, Collider.ColliderTag.TRAPS,
                new Vector3(0, Constants.TILE_SIZE / 4, 0),
                new Vector3(Constants.TILE_SIZE * colliderShrink, Constants.TILE_SIZE / 4, Constants.TILE_SIZE * colliderShrink));

        mSpikesPosition = position.cpy();
        mStartingPosition = position.cpy();
        mSpikesDownOffset = new Vector3(0, -Constants.TILE_SIZE / 2, 0);
        mTransitionAngle = 0;
        mCycleSpeed = 180f;
        mMinWaitTime = 3f;
        mWaitTimer = 0f;
        mWaiting = false;

        mHitDamage = 10;
        mSpikeAngleDamageThreshold = 60;

        mColliderCheckMaxDelay = 5;
        mColliderCheckDelay = 0;

        positionSpikes();
    }


    @Override
    public void update(float deltaTime) {
        //Check if the spike is in a delay timer
        if (mWaiting) {
            //Increment the delay timer and test if it has exceeded the delay time required
            mWaitTimer += deltaTime;
            if (mWaitTimer > mMinWaitTime) {
                mWaitTimer = 0;
                mWaiting = false;
            }
        } else {
            //Add to the transition based on the speed of angle cycling
            mTransitionAngle += mCycleSpeed * deltaTime;
            if (mTransitionAngle > 360) {
                mWaiting = true;
                mTransitionAngle = 0;
            }
            //Move the spikes
            positionSpikes();
        }

        //Check if the collider is in the threshold
        if (mTransitionAngle > mSpikeAngleDamageThreshold && mTransitionAngle < 360 - mSpikeAngleDamageThreshold) {
            mColliderCheckDelay++;
            if (mColliderCheckDelay > mColliderCheckMaxDelay) {
                //Attempt to damage any entities
                checkDamageCollisions(Collider.ColliderTag.ENTITIES);
                mColliderCheckDelay = 0;
            }
        }
    }

    /**
     * Checks to see if the base collider has hit any other colliders of the tag type and attempt
     * to damage them.
     *
     * @param tag of the colliders to check collision with
     */
    private void checkDamageCollisions(Collider.ColliderTag tag) {
        LinkedList<Collider> collisions = mCollisionManager.getCollisions(mBoxCollider, tag);
        if (collisions.size() > 0) {
            damageOtherLivingGameObjects(collisions);
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
     * Used to set the position of the spikes and the collider to the current position in the
     * movement cycle.
     */
    private void positionSpikes() {
        float positionScale = (float) Math.cos(Math.toRadians(mTransitionAngle));
        if (positionScale < 0) positionScale = 0;
        mSpikesPosition = mStartingPosition.cpy().add(mSpikesDownOffset.cpy().scl(positionScale));
        setPosition(mSpikesPosition);
        mBoxCollider.updatePosition();
    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        modelBatch.begin(mCameraController.getCamera());
        modelBatch.render(mSpikesBase);
        modelBatch.render(mSpikesSpikes, mCameraController.getEnvironment());
        modelBatch.end();

        CollisionManager.get().renderCollider(mBoxCollider);
    }

    @Override
    public void setPosition(Vector3 position) {
        mPosition = position.cpy();
        mSpikesSpikes.transform.setTranslation(position);
    }
}
