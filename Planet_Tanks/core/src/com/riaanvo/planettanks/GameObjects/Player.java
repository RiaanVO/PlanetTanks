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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.Physics.Collider;
import com.riaanvo.planettanks.Physics.SphereCollider;
import com.riaanvo.planettanks.managers.CollisionManager;
import com.riaanvo.planettanks.managers.GameObjectManager;

import java.util.LinkedList;

/**
 * This class creates a player and handles the users input. It controls the tank model that the player
 * sees and interacts with in the game.
 */

public class Player extends LivingGameObject {
    //Players tank model
    private TankController mTankController;

    //Colliders used to control player movement
    private CollisionManager mCollisionManager;
    private SphereCollider mBaseSphereCollider;
    private SphereCollider mXTestCollider;
    private SphereCollider mZTestCollider;

    private float mMoveSpeed;

    //Timers used to restrict how often a player can shoot
    private float mMinTimeBetweenShots;
    private float mShotTimer;

    //Used to control the players' tank movement
    private Touchpad mMovementTouchpad;
    private Touchpad mAimingTouchpad;

    public Player(TankController tankController) {
        super();
        setTag("Player");
        setHealth(3);

        //Setup the tanks model and set is firing direction
        mTankController = tankController;
        mTankController.setParent(this);
        mTankController.setTurretDirection(new Vector3(0, 0, 1));

        //Set up the main collider and register it with the collision manager
        mCollisionManager = CollisionManager.get();
        mBaseSphereCollider = new SphereCollider(this, Collider.ColliderTag.ENTITIES, Constants.TANK_COLLIDER_OFFSET, Constants.TANK_COLLIDER_RADIUS);
        mCollisionManager.addCollider(mBaseSphereCollider);
        //Used to test if the player can move
        mXTestCollider = new SphereCollider(this, Collider.ColliderTag.ENTITIES, Constants.TANK_COLLIDER_OFFSET, Constants.TANK_COLLIDER_RADIUS);
        mZTestCollider = new SphereCollider(this, Collider.ColliderTag.ENTITIES, Constants.TANK_COLLIDER_OFFSET, Constants.TANK_COLLIDER_RADIUS);


        mMoveSpeed = 2.5f;
        mMinTimeBetweenShots = 0.5f;
        mShotTimer = 0;
    }

    @Override
    public void update(float deltaTime) {
        if (isDead()) {
            handelDeath();
            return;
        }

        //TODO: remove the keyboard input
        //Get the players tank movement input
        Vector3 moveDirection = new Vector3();
        if (Gdx.input.isKeyPressed(Input.Keys.W)) moveDirection.z += -1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) moveDirection.z += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) moveDirection.x += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) moveDirection.x += -1;
        if (mMovementTouchpad != null) {
            moveDirection.x += mMovementTouchpad.getKnobPercentX() * 1;
            moveDirection.z += mMovementTouchpad.getKnobPercentY() * (-1);
        }
        //If the player has moved, move the tank
        if (moveDirection.x != 0 || moveDirection.z != 0) move(moveDirection, deltaTime);

        //TODO: remove the keyboard input
        //Get the players aiming direction
        Vector3 aimDirection = new Vector3();
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) aimDirection.z += -1;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) aimDirection.z += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) aimDirection.x += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) aimDirection.x += -1;
        if (mAimingTouchpad != null) {
            aimDirection.x += mAimingTouchpad.getKnobPercentX() * 1;
            aimDirection.z += mAimingTouchpad.getKnobPercentY() * (-1);
        }
        //Update the players aiming direction if it has changed
        if (aimDirection.x != 0 || aimDirection.z != 0) mTankController.setTurretDirection(aimDirection);

        mShotTimer += deltaTime;
        //TODO: remove this if statement
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            shoot();
        }
    }

    /**
     * Shoot the players tank and reset the shot timer
     */
    public void shoot() {
        if (mShotTimer > mMinTimeBetweenShots) {
            mShotTimer = 0;
            mTankController.shoot();
        }
    }

    /**
     * Attempts to move the tank in the direction provided. Uses the colliders to check if the
     * movement is allowed.
     *
     * @param direction the tank will move in
     * @param deltaTime the change in time since the last update
     */
    private void move(Vector3 direction, float deltaTime) {
        direction.nor();
        //Calculate the new position and set the base collider to that position
        Vector3 moveAdjustment = direction.cpy().scl(deltaTime * mMoveSpeed);
        Vector3 newPosition = moveAdjustment.cpy().add(getPosition());
        mBaseSphereCollider.setPosition(newPosition);

        boolean moveX = true;
        boolean moveZ = true;

        //Check if the main collider has hit either a wall or other tanks. Store the list for later use
        LinkedList<Collider> collisions = mCollisionManager.getCollisions(mBaseSphereCollider, Collider.ColliderTag.WALL);
        collisions.addAll(mCollisionManager.getCollisions(mBaseSphereCollider, Collider.ColliderTag.ENTITIES));
        if (collisions.size() > 0) {
            //Calculate the components of the movement
            Vector3 xMoveAdjustment = moveAdjustment.cpy();
            xMoveAdjustment.z = 0;
            Vector3 zMoveAdjustment = moveAdjustment.cpy();
            zMoveAdjustment.x = 0;

            //Move the test colliders to the adjusted position
            mXTestCollider.adjustPosition(xMoveAdjustment);
            mZTestCollider.adjustPosition(zMoveAdjustment);

            //Check if either component is obstructed
            if (mCollisionManager.getCollisionsInListBoolean(mXTestCollider, collisions)) {
                moveX = false;
            }
            if (mCollisionManager.getCollisionsInListBoolean(mZTestCollider, collisions)) {
                moveZ = false;
            }
        }

        //Reset the new position if the player can't move there
        if (!moveX) newPosition.x = getPosition().x;
        if (!moveZ) newPosition.z = getPosition().z;

        setPosition(newPosition);
        //Update the tanks body facing direction
        mTankController.setBodyDirection(direction);
    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        mTankController.renderTank(spriteBatch, modelBatch);
        //Attempt to render the tanks collider
        mCollisionManager.renderCollider(mBaseSphereCollider);
    }

    @Override
    public void setPosition(Vector3 position) {
        mPosition = position.cpy();
        mTankController.setPosition(position.cpy());
        mBaseSphereCollider.updatePosition();
        mXTestCollider.updatePosition();
        mZTestCollider.updatePosition();
    }

    public void setTouchPads(Touchpad movement, Touchpad aiming) {
        mMovementTouchpad = movement;
        mAimingTouchpad = aiming;
    }

    @Override
    protected void handelDeath() {
        if (!mDeathHandled) {
            //Detach this game object and collider from the managers
            GameObjectManager.get().removeGameObject(this);
            mCollisionManager.removeCollider(mBaseSphereCollider);
            mDeathHandled = true;
        }
    }
}
