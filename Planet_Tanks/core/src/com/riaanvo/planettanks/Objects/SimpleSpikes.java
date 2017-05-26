package com.riaanvo.planettanks.Objects;

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
 * Created by riaanvo on 26/5/17.
 */

public class SimpleSpikes extends GameObject{
    private ModelInstance mSpikesSpikes;
    private ModelInstance mSpikesBase;
    private CameraController mCameraController;
    private CollisionManager mCollisionManager;
    private BoxCollider mBoxCollider;

    private float transitionAngle;
    private float cycleSpeed;
    private float minWaitTime;
    private float waitTimer;
    private Vector3 spikesDownOffset;
    private Vector3 spikesPosition;
    private Vector3 startingPosition;
    private boolean waiting;

    private float spikeAngleDamageThreshold;
    private int hitDamage;

    public SimpleSpikes(Model spikesBase, Model spikesSpikes, Vector3 position){
        mCameraController = CameraController.get();
        mCollisionManager = CollisionManager.get();
        mSpikesBase = new ModelInstance(spikesBase);
        mSpikesSpikes = new ModelInstance(spikesSpikes);
        mSpikesBase.transform.setTranslation(position);
        setPosition(position);

        //GameObject gameObject, ColliderTag tag, Vector3 offset, Vector3 size -- size.cpy().scl(0.5f)
        float colliderShrink = 0.7f;
        mBoxCollider = new BoxCollider(this, Collider.ColliderTag.TRAPS, new Vector3(0,Constants.TILE_SIZE/4,0), new Vector3(Constants.TILE_SIZE * colliderShrink, Constants.TILE_SIZE/4, Constants.TILE_SIZE * colliderShrink));
        CollisionManager.get().addCollider(mBoxCollider);

        spikesPosition = position.cpy();
        startingPosition = position.cpy();
        spikesDownOffset = new Vector3(0, -Constants.TILE_SIZE/2, 0);
        transitionAngle = 0;
        cycleSpeed = 90f;
        minWaitTime = 3f;
        waitTimer = 0f;
        waiting = false;

        hitDamage = 10;
        spikeAngleDamageThreshold = 60;

        positionSpikes();
    }


    @Override
    public void update(float dt) {
        if(waiting){
            waitTimer += dt;
            if(waitTimer > minWaitTime){
                waitTimer = 0;
                waiting = false;
            }
        } else {
            transitionAngle += cycleSpeed * dt;
            if(transitionAngle > 360){
                waiting = true;
                transitionAngle = 0;
            }
            positionSpikes();
        }

        if(!waiting){
            if(transitionAngle > spikeAngleDamageThreshold && transitionAngle < 360 - spikeAngleDamageThreshold){
                checkDamageCollisions(Collider.ColliderTag.ENTITIES);
            }
        }

    }

    private void checkDamageCollisions(Collider.ColliderTag tag) {
        LinkedList<Collider> collisions = mCollisionManager.getCollisions(mBoxCollider, tag);
        if (collisions.size() > 0) {
            damageOtherLivingGameObjects(collisions);
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

    private void positionSpikes(){
        float positionScale = (float)Math.cos(Math.toRadians(transitionAngle));
        if(positionScale < 0) positionScale = 0;
        spikesPosition = startingPosition.cpy().add(spikesDownOffset.cpy().scl(positionScale));
        setPosition(spikesPosition);
        mBoxCollider.updatePosition();
    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        modelBatch.begin(mCameraController.getCamera());
        modelBatch.render(mSpikesBase, mCameraController.getEnvironment());
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
