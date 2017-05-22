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

/**
 * Created by riaanvo on 15/5/17.
 */

public class Player extends GameObject {
    private ModelInstance mPlayerTankBase;
    private ModelInstance mPlayerTankTurret;
    private CameraController mCameraController;

    private float speed = 5f;

    private float bodyOrientation;
    private float turretOrientation;

    public Player(Model playerTankBase, Model playerTankTurret){
        super();
        mCameraController = CameraController.get();

        ColorAttribute TcolorAttr = new ColorAttribute(ColorAttribute.Diffuse, Color.BLUE);

        mPlayerTankBase = new ModelInstance(playerTankBase);
        mPlayerTankBase.transform.translate(getPosition());
        mPlayerTankBase.materials.get(0).set(TcolorAttr);

        mPlayerTankTurret = new ModelInstance(playerTankTurret);
        mPlayerTankTurret.transform.translate(getPosition());

        bodyOrientation = getOrientation();
        turretOrientation = getOrientation();
    }

    @Override
    public void update(float deltaTime) {
        Vector3 moveDirection = new Vector3();
        if(Gdx.input.isKeyPressed(Input.Keys.W)) moveDirection.z += -1;
        if(Gdx.input.isKeyPressed(Input.Keys.S)) moveDirection.z += 1;
        if(Gdx.input.isKeyPressed(Input.Keys.D)) moveDirection.x += 1;
        if(Gdx.input.isKeyPressed(Input.Keys.A)) moveDirection.x += -1;
        if(moveDirection.x != 0 || moveDirection.z != 0) move(moveDirection, deltaTime);


        Vector3 aimDirection = new Vector3();
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) aimDirection.z += -1;
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) aimDirection.z += 1;
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) aimDirection.x += 1;
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) aimDirection.x += -1;
        if(aimDirection.x != 0 || aimDirection.z != 0) rotateTurret(aimDirection);

    }

    private void move(Vector3 direction, float deltaTime){
        direction.nor();
        Vector3 newPosition = direction.cpy().scl(deltaTime * speed).add(getPosition());
        setPosition(newPosition);
        mPlayerTankBase.transform.setTranslation(newPosition);
        rotateBody(direction);
    }

    private void rotateBody(Vector3 direction){
        float newOrientation = calculateOrientation(direction);
        float changeInOrientation = newOrientation - bodyOrientation;
        mPlayerTankBase.transform.rotate(Vector3.Y, changeInOrientation);
        bodyOrientation = newOrientation;
    }

    private void rotateTurret(Vector3 direction){
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
    }

    @Override
    public void setPosition(Vector3 position) {
        super.setPosition(position);
        mPlayerTankBase.transform.setTranslation(getPosition());
        mPlayerTankTurret.transform.setTranslation(getPosition());
    }
}
