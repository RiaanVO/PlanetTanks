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
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.Physics.Collider;
import com.riaanvo.planettanks.Physics.SphereCollider;
import com.riaanvo.planettanks.managers.CollisionManager;
import com.riaanvo.planettanks.managers.GameObjectManager;
import com.riaanvo.planettanks.managers.LevelManager;

import java.util.LinkedList;

/**
 * This class provides all the logic to create and operate a basic stationary enemy tank. It extends
 * from Living game object and thus is a kill-able object in the game.
 */

public class BasicEnemy extends LivingGameObject {
    //The controller for the tank model
    private TankController mTankController;

    //Used for collision detection and simple player locating
    private float mColliderRadius;
    private Vector3 mColliderOffset;
    private CollisionManager mCollisionManager;
    private SphereCollider mBaseSphereCollider;
    private SphereCollider mSearchSphereCollider;

    //Used to determine if the tank should fire
    private Vector3 mFireDirection;
    private float mPlayerDetectRadius;
    private boolean mPlayerInRange;
    private boolean mAimingAtPlayer;
    private boolean mClearLineOfFire;
    private float mMinTimeBetweenShots;
    private float mShotTimer;

    //Used for controlling where the tank is aiming
    private float mAimingSpeed;
    private float mAimingThreshold;

    //Used for scoring
    private int mPointsOnKilled;

    public BasicEnemy(TankController tankController) {
        super();
        setTag(Constants.TAG_BASIC_STATIC_ENEMY);
        setHealth(1);

        mTankController = tankController;
        mTankController.setParent(this);

        //Initialise the colliders and register the main collider with the collision manager
        mColliderRadius = 0.8f;
        mColliderOffset = new Vector3(0, 0.5f, 0);
        mPlayerDetectRadius = 10f;
        mCollisionManager = CollisionManager.get();
        mBaseSphereCollider = new SphereCollider(this, Collider.ColliderTag.ENTITIES, mColliderOffset, mColliderRadius);
        mCollisionManager.addCollider(mBaseSphereCollider);
        mSearchSphereCollider = new SphereCollider(this, Collider.ColliderTag.ENTITIES, mColliderOffset, mPlayerDetectRadius);

        //Set up the base variables used for controlling the tank
        mPlayerInRange = false;
        mAimingAtPlayer = false;
        mClearLineOfFire = true;
        mMinTimeBetweenShots = 1f;
        mShotTimer = 0;

        mAimingSpeed = 90f;
        mAimingThreshold = 5f;

        mPointsOnKilled = 100;
    }


    @Override
    public void update(float dt) {
        if (isDead()) {
            handelDeath();
            return;
        }

        mShotTimer += dt;

        //Check if the player is in range and rotate to face them
        if (mPlayerInRange = isPlayerInRange()) {
            rotateToTarget(dt);
            //Check if the tank is aiming at the player
            if (mAimingAtPlayer = isAimingAtPlayer()) {
                //TODO: Implement ray-casting to check if there is an obstacle in the way of the shot
                mClearLineOfFire = true;
            }
        }

        //Shoot at the player if all the conditions are met and reset the shot timer
        if (mPlayerInRange && mAimingAtPlayer && mClearLineOfFire) {
            if (mShotTimer > mMinTimeBetweenShots) {
                mShotTimer = 0;
                mTankController.shoot();
            }
        }

    }

    /**
     * Checks to see if a game object with the tag of Player is in range of this tank.
     *
     * @return is the player in range of the tank
     */
    private boolean isPlayerInRange() {
        //Get a list of entity colliders from the collision manager
        LinkedList<Collider> collisions = mCollisionManager.getCollisions(mSearchSphereCollider, Collider.ColliderTag.ENTITIES);
        for (Collider collider : collisions) {
            //Check if the collider is attached to the player game object and calculate the firing direction
            if (collider.getGameObject().compareTag(Constants.TAG_PLAYER)) {
                mFireDirection = collider.getGameObject().getPosition().cpy().sub(getPosition()).nor();
                return true;
            }
        }
        return false;
    }

    /**
     * Rotates the tank turret to the current firing direction
     *
     * @param deltaTime The time between each update call
     */
    private void rotateToTarget(float deltaTime) {
        //Calculate the change in the tanks orientation and use the shortest turning path
        float changeInOrientation = calculateOrientation(mFireDirection) - mTankController.getTurretOrientation();
        if (Math.abs(changeInOrientation) > 180) changeInOrientation *= -1;

        //Check if the is aiming at the player. If not use full aiming speed else use a reduced aiming speed
        if (!isAimingAtPlayer()) {
            changeInOrientation /= Math.abs(changeInOrientation);
            changeInOrientation *= mAimingSpeed * deltaTime;
        } else {
            changeInOrientation /= 10;
        }

        //set the tanks orientation to the new calculated orientation
        mTankController.setTurretOrientation(mTankController.getTurretOrientation() + changeInOrientation);
    }

    /**
     * Checks to see if the tanks current aiming direction is within the aiming threshold for shooting
     *
     * @return is the tank aiming at the player
     */
    private boolean isAimingAtPlayer() {
        //Calculate the change in orientation between the actual aiming direction and the desired aiming direction
        return Math.abs(calculateOrientation(mFireDirection) - mTankController.getTurretOrientation()) < mAimingThreshold;
    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        //Draw the tank model
        mTankController.renderTank(spriteBatch, modelBatch);

        //Attempt to draw the colliders
        mCollisionManager.renderCollider(mBaseSphereCollider);
        mCollisionManager.renderCollider(mSearchSphereCollider);
    }

    @Override
    protected void handelDeath() {
        if (!deathHandled) {
            //Remove from the update and render list
            GameObjectManager.get().removeGameObject(this);
            //Remove from the collision manager
            mCollisionManager.removeCollider(mBaseSphereCollider);
            //Notify that tank has been killed and add points
            LevelManager.get().EnemyKilled(mPointsOnKilled);
            deathHandled = true;
        }
    }

    @Override
    public void setPosition(Vector3 position) {
        //Set the game objects position and the position of tank controller
        mPosition = position.cpy();
        mTankController.setPosition(position.cpy());
        //Update the position of the colliders from the new tank position
        mBaseSphereCollider.updatePosition();
        mSearchSphereCollider.updatePosition();
    }
}
