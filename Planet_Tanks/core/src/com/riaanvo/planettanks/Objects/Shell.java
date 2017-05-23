package com.riaanvo.planettanks.Objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.riaanvo.planettanks.Physics.Collider;
import com.riaanvo.planettanks.Physics.SphereCollider;
import com.riaanvo.planettanks.managers.CollisionManager;
import com.riaanvo.planettanks.managers.GameObjectManager;

/**
 * Created by riaanvo on 23/5/17.
 */

public class Shell extends GameObject {
    private ModelInstance mShell;
    private CameraController mCameraController;
    private CollisionManager mCollisionManager;
    private SphereCollider mBaseSphereCollider;
    private SphereCollider mXTestCollider;
    private SphereCollider mZTestCollider;

    private Vector3 moveDirection;
    private float speed;

    private final int mMaxBounceCount;
    private int mCurrentBounceCount;

    public Shell(Model shell, Vector3 startingPosition, Vector3 startingDirection){
        super();
        mShell = new ModelInstance(shell);
        mCameraController = CameraController.get();
        moveDirection = startingDirection.cpy();

        float colliderRadius = 0.1f;
        Vector3 colliderOffset = new Vector3(0,0,0);
        mCollisionManager = CollisionManager.get();
        mBaseSphereCollider = new SphereCollider(this, Collider.ColliderTag.PROJECTILES, colliderOffset, colliderRadius);
        mCollisionManager.addCollider(mBaseSphereCollider);
        mXTestCollider = new SphereCollider(this, Collider.ColliderTag.PROJECTILES, colliderOffset, colliderRadius);
        mZTestCollider = new SphereCollider(this, Collider.ColliderTag.PROJECTILES, colliderOffset, colliderRadius);

        setPosition(startingPosition);

        mMaxBounceCount = 100;
        mCurrentBounceCount = 0;
        speed = 5f;
    }

    @Override
    public void update(float dt) {
        move(moveDirection, dt);
        if(mCurrentBounceCount > mMaxBounceCount){
            mCollisionManager.removeCollider(mBaseSphereCollider);
            GameObjectManager.get().removeGameObject(this);
        }
    }

    private void move(Vector3 direction, float deltaTime){
        direction.nor();
        Vector3 moveAdjustment = direction.cpy().scl(deltaTime * speed);
        Vector3 newPosition = moveAdjustment.cpy().add(getPosition());

        mBaseSphereCollider.setPosition(newPosition);
        if(mCollisionManager.getCollisions(mBaseSphereCollider, Collider.ColliderTag.WALL).size() > 0){
            Vector3 xMoveAdjustment = moveAdjustment.cpy();
            xMoveAdjustment.z = 0;
            Vector3 zMoveAdjustment = moveAdjustment.cpy();
            zMoveAdjustment.x = 0;

            mXTestCollider.adjustPosition(xMoveAdjustment);
            mZTestCollider.adjustPosition(zMoveAdjustment);

            if(mCollisionManager.getCollisions(mXTestCollider, Collider.ColliderTag.WALL). size() > 0){
                newPosition.x = getPosition().x - xMoveAdjustment.x;
                moveDirection.x = -moveDirection.x;
                mCurrentBounceCount++;
            }

            if(mCollisionManager.getCollisions(mZTestCollider, Collider.ColliderTag.WALL). size() > 0){
                newPosition.z = getPosition().z - zMoveAdjustment.z;
                moveDirection.z = -moveDirection.z;
                mCurrentBounceCount++;
            }
        }

        setPosition(newPosition);
        rotateShell(direction);
    }

    private void rotateShell(Vector3 direction){
        float newOrientation = calculateOrientation(direction);
        float changeInOrientation = newOrientation - mOrientation;
        mShell.transform.rotate(Vector3.Y, changeInOrientation);
        mOrientation = newOrientation;
    }

    private float calculateOrientation(Vector3 direction){
        float newOrientation;
        if (direction.x != 0) {
            if (direction.x < 0) {
                newOrientation = 360 - (float) Math.toDegrees(Math.atan2(direction.x, direction.z)) * -1;
            } else {
                newOrientation = (float) Math.toDegrees(Math.atan2(direction.x, direction.z));
            }
        } else {
            if(direction.z > 0){
                newOrientation = 0;
            } else {
                newOrientation = 180;
            }
        }
        return newOrientation;
    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        modelBatch.begin(mCameraController.getCamera());
        modelBatch.render(mShell, mCameraController.getEnvironment());
        modelBatch.end();

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
}
