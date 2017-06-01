package com.riaanvo.planettanks.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.riaanvo.planettanks.Physics.Collider;
import com.riaanvo.planettanks.Physics.SphereCollider;
import com.riaanvo.planettanks.managers.CollisionManager;
import com.riaanvo.planettanks.managers.GameObjectManager;

import java.util.LinkedList;

/**
 * Created by riaanvo on 15/5/17.
 */

public class Player extends LivingGameObject {

    private TankController mTankController;
    private GameObjectManager mGameObjectManager;

    private float speed;

    private Touchpad movementTouchpad;
    private Touchpad aimingTouchpad;

    private CollisionManager mCollisionManager;
    private SphereCollider mBaseSphereCollider;
    private SphereCollider mXTestCollider;
    private SphereCollider mZTestCollider;

    private float mMinTimeBetweenShots;
    private float mShotTimer;


    public Player(TankController tankController){
        super();
        mTankController = tankController;
        mTankController.setParent(this);
        mTankController.setTurretDirection(new Vector3(0,0,1));

        speed = 2.5f;

        mCollisionManager = CollisionManager.get();
        float colliderRadius = 0.8f;
        Vector3 colliderOffset = new Vector3(0, 0.5f, 0);
        mBaseSphereCollider = new SphereCollider(this, Collider.ColliderTag.ENTITIES, colliderOffset, colliderRadius);
        mCollisionManager.addCollider(mBaseSphereCollider);

        mXTestCollider = new SphereCollider(this, Collider.ColliderTag.ENTITIES, colliderOffset, colliderRadius);
        mZTestCollider = new SphereCollider(this, Collider.ColliderTag.ENTITIES, colliderOffset, colliderRadius);

        mBaseSphereCollider.updatePosition();
        mXTestCollider.updatePosition();
        mZTestCollider.updatePosition();

        mGameObjectManager = GameObjectManager.get();
        setTag("Player");
        setHealth(3);

        mMinTimeBetweenShots = 1f;
        mShotTimer = 0;
    }

    @Override
    public void update(float deltaTime) {
        if(isDead()){
            handelDeath();
            return;
        }

        Vector3 moveDirection = new Vector3();
            if (Gdx.input.isKeyPressed(Input.Keys.W)) moveDirection.z += -1;
            if (Gdx.input.isKeyPressed(Input.Keys.S)) moveDirection.z += 1;
            if (Gdx.input.isKeyPressed(Input.Keys.D)) moveDirection.x += 1;
            if (Gdx.input.isKeyPressed(Input.Keys.A)) moveDirection.x += -1;
        if(movementTouchpad != null) {
            moveDirection.x += movementTouchpad.getKnobPercentX() * 1;
            moveDirection.z += movementTouchpad.getKnobPercentY() * (-1);
        }
        if(moveDirection.x != 0 || moveDirection.z != 0) move(moveDirection, deltaTime);


        Vector3 aimDirection = new Vector3();
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) aimDirection.z += -1;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) aimDirection.z += 1;
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) aimDirection.x += 1;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) aimDirection.x += -1;
        if(aimingTouchpad != null) {
            aimDirection.x += aimingTouchpad.getKnobPercentX() * 1;
            aimDirection.z += aimingTouchpad.getKnobPercentY() * (-1);
        }
        if(aimDirection.x != 0 || aimDirection.z != 0) mTankController.setTurretDirection(aimDirection);


        mShotTimer += deltaTime;
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            shoot();
        }
    }

    public void shoot(){
        if(mShotTimer > mMinTimeBetweenShots){
            mShotTimer = 0;
            mTankController.shoot();
        }
    }

    private void move(Vector3 direction, float deltaTime){
        direction.nor();
        Vector3 moveAdjustment = direction.cpy().scl(deltaTime * speed);
        Vector3 newPosition = moveAdjustment.cpy().add(getPosition());
        mBaseSphereCollider.setPosition(newPosition);

        boolean moveX = true;
        boolean moveZ = true;

        LinkedList<Collider> collisions = mCollisionManager.getCollisions(mBaseSphereCollider, Collider.ColliderTag.WALL);
        collisions.addAll(mCollisionManager.getCollisions(mBaseSphereCollider, Collider.ColliderTag.ENTITIES));
        if(collisions.size() > 0){
            Vector3 xMoveAdjustment = moveAdjustment.cpy();
            xMoveAdjustment.z = 0;
            Vector3 zMoveAdjustment = moveAdjustment.cpy();
            zMoveAdjustment.x = 0;

            mXTestCollider.adjustPosition(xMoveAdjustment);
            mZTestCollider.adjustPosition(zMoveAdjustment);


            if(mCollisionManager.getCollisionsInListBoolean(mXTestCollider, collisions)){
                moveX = false;
            }
            if(mCollisionManager.getCollisionsInListBoolean(mZTestCollider, collisions)){
                moveZ = false;
            }
        }

        if(!moveX) newPosition.x = getPosition().x;
        if(!moveZ) newPosition.z = getPosition().z;

        setPosition(newPosition);
        mTankController.setBodyDirection(direction);
    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        mTankController.renderTank(spriteBatch, modelBatch);
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

    public void setTouchPads(Touchpad movement, Touchpad aiming){
        movementTouchpad = movement;
        aimingTouchpad = aiming;
    }

    @Override
    protected void handelDeath() {
        if(!deathHandled) {
            mGameObjectManager.removeGameObject(this);
            mCollisionManager.removeCollider(mBaseSphereCollider);
            deathHandled = true;
        }
    }
}
