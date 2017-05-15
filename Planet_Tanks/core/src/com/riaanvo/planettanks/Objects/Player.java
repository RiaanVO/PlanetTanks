package com.riaanvo.planettanks.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by riaanvo on 15/5/17.
 */

public class Player extends GameObject {
    private ModelInstance mPlayerModel;
    private CameraController mCameraController;

    private float speed = 5f;

    public Player(ModelInstance playerModel){
        super();
        mPlayerModel = playerModel;
        mPlayerModel.transform.translate(getPosition());
        mCameraController = CameraController.get();
    }

    @Override
    public void update(float deltaTime) {
        float moveX = 0;
        float moveZ = 0;
        if(Gdx.input.isKeyPressed(Input.Keys.W)){
            moveZ = -1;
        } else if(Gdx.input.isKeyPressed(Input.Keys.S)){
            moveZ = 1;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            moveX = 1;
        } else if(Gdx.input.isKeyPressed(Input.Keys.A)){
            moveX = -1;
        }

        if(moveX != 0 || moveZ != 0) move(moveX, moveZ, deltaTime);
    }

    private void move(float x, float z, float deltaTime){
        Vector3 direction = new Vector3(x, 0, z);
        direction.nor();
        Vector3 newPosition = direction.scl(deltaTime * speed).add(getPosition());
        setPosition(newPosition);
        mPlayerModel.transform.setTranslation(newPosition);
    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        modelBatch.begin(mCameraController.getCamera());
        modelBatch.render(mPlayerModel);
        modelBatch.end();
    }

    @Override
    public void setPosition(Vector3 position) {
        super.setPosition(position);
        mPlayerModel.transform.translate(getPosition());
    }
}
