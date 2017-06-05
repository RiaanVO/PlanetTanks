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
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.riaanvo.planettanks.Physics.BoxCollider;
import com.riaanvo.planettanks.Physics.Collider;
import com.riaanvo.planettanks.managers.CollisionManager;

/**
 * This class creates and renders a wall segment at the position provided. It also maintains a collider
 * which other colliders can interact with.
 */

public class WallSegment extends GameObject {
    private ModelInstance mWallSegment;
    private CameraController mCameraController;

    //Collider used to prevent objects from passing through
    private BoxCollider mBoxCollider;

    public WallSegment(Model wallSegment, Vector3 position, Vector3 size) {
        mCameraController = CameraController.get();
        mWallSegment = new ModelInstance(wallSegment);
        mWallSegment.transform.setTranslation(position);
        setPosition(position);

        //Create the box collider at this position and register it with the collision manager
        mBoxCollider = new BoxCollider(this, Collider.ColliderTag.WALL, new Vector3(0, 0, 0), size);
        CollisionManager.get().addCollider(mBoxCollider);
    }


    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        modelBatch.begin(mCameraController.getCamera());
        modelBatch.render(mWallSegment, mCameraController.getEnvironment());
        modelBatch.end();

        //Attempt to render the collider
        CollisionManager.get().renderCollider(mBoxCollider);
    }
}
