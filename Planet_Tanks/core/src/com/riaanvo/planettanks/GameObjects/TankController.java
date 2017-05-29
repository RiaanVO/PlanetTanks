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
 * Created by riaanvo on 25/5/17.
 */

public class TankController {
    private com.riaanvo.planettanks.GameObjects.GameObject mParent;
    private ModelInstance mTankBase;
    private ModelInstance mTankTurret;

    private float bodyOrientation;
    private float turretOrientation;

    private Vector3 aimingDirection;

    private float bulletStartOffset;
    private float bulletStartHeight;

    private GameObjectManager mGameObjectManager;
    private CameraController mCameraController;

    public TankController(Model tankBase, Model tankTurret, ColorAttribute tankColour){
        mCameraController = CameraController.get();
        mGameObjectManager = GameObjectManager.get();

        mTankBase = new ModelInstance(tankBase);
        if(tankColour != null) mTankBase.materials.get(0).set(tankColour);
        mTankTurret = new ModelInstance(tankTurret);

        bulletStartOffset = 1.1f;
        bulletStartHeight = 0.8f;
    }

    public void shoot(){
        if(aimingDirection == null) return;
        Vector3 startingPosition = mParent.getPosition().cpy();
        startingPosition.y = bulletStartHeight;
        startingPosition.add(aimingDirection.cpy().scl(bulletStartOffset));
        mGameObjectManager.addGameObject(new com.riaanvo.planettanks.GameObjects.Shell(ContentManager.get().getShell(), startingPosition, aimingDirection));
    }


    public void renderTank(SpriteBatch spriteBatch, ModelBatch modelBatch){
        modelBatch.begin(mCameraController.getCamera());
        modelBatch.render(mTankBase, mCameraController.getEnvironment());
        modelBatch.render(mTankTurret, mCameraController.getEnvironment());
        modelBatch.end();
    }

    public void setBodyDirection(Vector3 direction){
        float changeInOrientation = mParent.calculateOrientation(direction) - bodyOrientation;
        mTankBase.transform.rotate(Vector3.Y, changeInOrientation);
        bodyOrientation = mParent.calculateOrientation(direction);
    }

    public void setTurretDirection(Vector3 newDirection){
        aimingDirection = newDirection.cpy();
        float newOrientation = mParent.calculateOrientation(aimingDirection);
        rotateTurret(newOrientation);
    }

    public void setPosition(Vector3 newPosition){
        mTankBase.transform.setTranslation(newPosition);
        mTankTurret.transform.setTranslation(newPosition);
    }

    public void setParent(com.riaanvo.planettanks.GameObjects.GameObject parent){
        mParent = parent;
    }

    public float getTurretOrientation(){
        return turretOrientation;
    }

    public void setTurretOrientation(float newOrientation){
        rotateTurret(newOrientation);
        aimingDirection = mParent.calculateDirection(turretOrientation);
    }

    private void rotateTurret(float newOrientation){
        float changeInOrientation = newOrientation - turretOrientation;
        mTankTurret.transform.rotate(Vector3.Y, changeInOrientation);
        if(newOrientation > 360){
            newOrientation -= 360;
        }
        if(newOrientation < 0){
            newOrientation += 360;
        }
        turretOrientation = newOrientation;
    }
}
