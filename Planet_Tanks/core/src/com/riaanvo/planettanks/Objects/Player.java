package com.riaanvo.planettanks.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.riaanvo.planettanks.Physics.Collider;
import com.riaanvo.planettanks.Physics.SphereCollider;
import com.riaanvo.planettanks.managers.CollisionManager;
import com.riaanvo.planettanks.managers.ContentManager;
import com.riaanvo.planettanks.managers.GameObjectManager;

/**
 * Created by riaanvo on 15/5/17.
 */

public class Player extends GameObject {
    private ModelInstance mPlayerTankBase;
    private ModelInstance mPlayerTankTurret;
    private CameraController mCameraController;

    private float speed;

    private float bodyOrientation;
    private float turretOrientation;

    private Touchpad movementTouchpad;
    private Touchpad aimingTouchpad;

    private CollisionManager mCollisionManager;
    private SphereCollider mBaseSphereCollider;
    private SphereCollider mXTestCollider;
    private SphereCollider mZTestCollider;

    private GameObjectManager mGameObjectManager;
    private Vector3 aimingDirection;
    private float bulletStartOffset;
    private float bulletStartHeight;
    private float mMinTimeBetweenShots;
    private float mShotTimer;

    public Player(Model playerTankBase, Model playerTankTurret){
        super();
        ColorAttribute TcolorAttr = new ColorAttribute(ColorAttribute.Diffuse, Color.BLUE);
        mPlayerTankBase = new ModelInstance(playerTankBase);
        mPlayerTankBase.transform.translate(getPosition());
        mPlayerTankBase.materials.get(0).set(TcolorAttr);

        mPlayerTankTurret = new ModelInstance(playerTankTurret);
        mPlayerTankTurret.transform.translate(getPosition());

        mCameraController = CameraController.get();
        speed = 5f;

        bodyOrientation = getOrientation();
        turretOrientation = getOrientation();

        //GameObject gameObject, ColliderTag tag, Vector3 offSet, float radius
        mCollisionManager = CollisionManager.get();
        float colliderRadius = 1f;
        Vector3 colliderOffset = new Vector3(0, 1, 0);
        mBaseSphereCollider = new SphereCollider(this, Collider.ColliderTag.ENTITIES, colliderOffset, colliderRadius);
        mCollisionManager.addCollider(mBaseSphereCollider);

        mXTestCollider = new SphereCollider(this, Collider.ColliderTag.ENTITIES, colliderOffset, colliderRadius);
        mZTestCollider = new SphereCollider(this, Collider.ColliderTag.ENTITIES, colliderOffset, colliderRadius);

        mBaseSphereCollider.updatePosition();
        mXTestCollider.updatePosition();
        mZTestCollider.updatePosition();

        mGameObjectManager = GameObjectManager.get();
        rotateTurret(new Vector3(0,0,-1));
        bulletStartOffset = 1f;
        bulletStartHeight = 1f;
        mMinTimeBetweenShots = 0.1f;
        mShotTimer = 0;
    }

    @Override
    public void update(float deltaTime) {
        Vector3 moveDirection = new Vector3();
        if(movementTouchpad == null) {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) moveDirection.z += -1;
            if (Gdx.input.isKeyPressed(Input.Keys.S)) moveDirection.z += 1;
            if (Gdx.input.isKeyPressed(Input.Keys.D)) moveDirection.x += 1;
            if (Gdx.input.isKeyPressed(Input.Keys.A)) moveDirection.x += -1;
        } else {
            moveDirection.x += movementTouchpad.getKnobPercentX()*1;
            moveDirection.z += movementTouchpad.getKnobPercentY()*(-1);
        }
        if(moveDirection.x != 0 || moveDirection.z != 0) move(moveDirection, deltaTime);


        Vector3 aimDirection = new Vector3();
        if(aimingTouchpad == null) {
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) aimDirection.z += -1;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) aimDirection.z += 1;
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) aimDirection.x += 1;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) aimDirection.x += -1;
        } else {
            aimDirection.x += aimingTouchpad.getKnobPercentX() * 1;
            aimDirection.z += aimingTouchpad.getKnobPercentY() * (-1);
        }
        if(aimDirection.x != 0 || aimDirection.z != 0) rotateTurret(aimDirection);


        mShotTimer += deltaTime;
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            if(mShotTimer > mMinTimeBetweenShots){
                mShotTimer = 0;
                Vector3 startingPosition = getPosition().cpy();
                startingPosition.y = bulletStartHeight;
                startingPosition.add(aimingDirection.cpy().scl(bulletStartOffset));
                mGameObjectManager.addGameObject(new Shell(ContentManager.get().getShell(), startingPosition, aimingDirection));
            }
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
                newPosition.x = getPosition().x;
            }

            if(mCollisionManager.getCollisions(mZTestCollider, Collider.ColliderTag.WALL). size() > 0){
                newPosition.z = getPosition().z;
            }
        }

        setPosition(newPosition);
        mPlayerTankBase.transform.setTranslation(newPosition);

        mBaseSphereCollider.updatePosition();
        mXTestCollider.updatePosition();
        mZTestCollider.updatePosition();
        rotateBody(direction);
    }

    private void rotateBody(Vector3 direction){
        float newOrientation = calculateOrientation(direction);
        float changeInOrientation = newOrientation - bodyOrientation;
        mPlayerTankBase.transform.rotate(Vector3.Y, changeInOrientation);
        bodyOrientation = newOrientation;
    }

    private void rotateTurret(Vector3 direction){
        aimingDirection = direction.cpy();
        float newOrientation = calculateOrientation(direction);
        float changeInOrientation = newOrientation - turretOrientation;
        mPlayerTankTurret.transform.rotate(Vector3.Y, changeInOrientation);
        turretOrientation = newOrientation;
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
        modelBatch.render(mPlayerTankBase, mCameraController.getEnvironment());
        modelBatch.render(mPlayerTankTurret, mCameraController.getEnvironment());
        modelBatch.end();

        mCollisionManager.renderCollider(mBaseSphereCollider);
        //mCollisionManager.renderCollider(mXTestCollider);
        //mCollisionManager.renderCollider(mZTestCollider);
    }

    @Override
    public void setPosition(Vector3 position) {
        mPosition = position.cpy();
        mPlayerTankBase.transform.setTranslation(getPosition());
        mPlayerTankTurret.transform.setTranslation(getPosition());
        mBaseSphereCollider.updatePosition();
    }

    public void setTouchPads(Touchpad movement, Touchpad aiming){
        movementTouchpad = movement;
        aimingTouchpad = aiming;
    }
}
