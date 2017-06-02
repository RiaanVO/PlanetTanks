package com.riaanvo.planettanks.GameObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.riaanvo.planettanks.Physics.Collider;
import com.riaanvo.planettanks.Physics.SphereCollider;
import com.riaanvo.planettanks.managers.CollisionManager;
import com.riaanvo.planettanks.managers.GameObjectManager;

import java.util.LinkedList;

/**
 * Created by riaanvo on 23/5/17.
 */

public class Shell extends LivingGameObject {
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
    private int hitDamage;

    public Shell(Model shell, Vector3 startingPosition, Vector3 startingDirection) {
        super();
        mShell = new ModelInstance(shell);
        mCameraController = CameraController.get();
        moveDirection = startingDirection.cpy();

        float colliderRadius = 0.1f;
        Vector3 colliderOffset = new Vector3(0, 0, 0);
        mCollisionManager = CollisionManager.get();
        mBaseSphereCollider = new SphereCollider(this, Collider.ColliderTag.PROJECTILES, colliderOffset, colliderRadius);
        mCollisionManager.addCollider(mBaseSphereCollider);
        mXTestCollider = new SphereCollider(this, Collider.ColliderTag.PROJECTILES, colliderOffset, colliderRadius);
        mZTestCollider = new SphereCollider(this, Collider.ColliderTag.PROJECTILES, colliderOffset, colliderRadius);

        setPosition(startingPosition);

        mMaxBounceCount = 1;
        mCurrentBounceCount = 0;
        speed = 3.5f;
        setTag("Basic shell");
        setHealth(1);
        hitDamage = 1;
    }

    @Override
    public void update(float dt) {
        if (isDead()) {
            handelDeath();
            return;
        }

        move(moveDirection, dt);
        checkDamageCollisions(Collider.ColliderTag.ENTITIES);
        checkDamageCollisions(Collider.ColliderTag.PROJECTILES);

        if (mCurrentBounceCount > mMaxBounceCount) {
            takeDamage(getHealth());
        }
    }

    private void move(Vector3 direction, float deltaTime) {
        direction.nor();
        Vector3 moveAdjustment = direction.cpy().scl(deltaTime * speed);
        Vector3 newPosition = moveAdjustment.cpy().add(getPosition());
        mBaseSphereCollider.setPosition(newPosition);

        LinkedList<Collider> collisions = mCollisionManager.getCollisions(mBaseSphereCollider, Collider.ColliderTag.WALL);
        if(collisions.size() > 0){
            Vector3 xMoveAdjustment = moveAdjustment.cpy();
            xMoveAdjustment.z = 0;
            Vector3 zMoveAdjustment = moveAdjustment.cpy();
            zMoveAdjustment.x = 0;

            mXTestCollider.adjustPosition(xMoveAdjustment);
            mZTestCollider.adjustPosition(zMoveAdjustment);


            if(mCollisionManager.getCollisionsInListBoolean(mXTestCollider, collisions)){
                newPosition.x = getPosition().x - xMoveAdjustment.x;
                moveDirection.x = -moveDirection.x;
                mCurrentBounceCount++;
            }
            if(mCollisionManager.getCollisionsInListBoolean(mZTestCollider, collisions)){
                newPosition.z = getPosition().z - zMoveAdjustment.z;
                moveDirection.z = -moveDirection.z;
                mCurrentBounceCount++;
            }
        }

        if (mCurrentBounceCount > mMaxBounceCount) return;

        setPosition(newPosition);
        rotateShell(direction);
    }

    private void checkDamageCollisions(Collider.ColliderTag tag) {
        LinkedList<Collider> collisions = mCollisionManager.getCollisions(mBaseSphereCollider, tag);
        if (collisions.size() > 0) {
            damageOtherLivingGameObjects(collisions);
            takeDamage(getHealth());
        }
    }

    private void damageOtherLivingGameObjects(LinkedList<Collider> others) {
        for (Collider collider : others) {
            LivingGameObject livingGameObject = ((LivingGameObject) collider.getGameObject());
            if (livingGameObject != null) {
                livingGameObject.takeDamage(hitDamage);
            }
        }
    }

    private void rotateShell(Vector3 direction) {
        float newOrientation = calculateOrientation(direction);
        float changeInOrientation = newOrientation - mOrientation;
        mShell.transform.rotate(Vector3.Y, changeInOrientation);
        mOrientation = newOrientation;
    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        modelBatch.begin(mCameraController.getCamera());
        modelBatch.render(mShell);//, mCameraController.getEnvironment());
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

    @Override
    protected void handelDeath() {
        if (!deathHandled) {
            mCollisionManager.removeCollider(mBaseSphereCollider);
            GameObjectManager.get().removeGameObject(this);
            deathHandled = true;
        }
    }
}
