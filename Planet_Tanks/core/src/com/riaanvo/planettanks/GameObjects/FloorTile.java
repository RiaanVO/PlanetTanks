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

/**
 * This class creates and renders an individual floor tile in a level.
 */

public class FloorTile extends GameObject {
    private ModelInstance mFloorTile;
    private CameraController mCameraController;
    private float mTileRotation;

    public FloorTile(Model floorTile, Vector3 position) {
        mCameraController = CameraController.get();

        //Create an instance of the tile model, rotate it and set its position
        mFloorTile = new ModelInstance(floorTile);
        mTileRotation = -90;
        mFloorTile.transform.rotate(Vector3.X, mTileRotation);
        mFloorTile.transform.setTranslation(position);
        setPosition(position);
    }

    @Override
    public void update(float deltaTime) {}

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        modelBatch.begin(mCameraController.getCamera());
        modelBatch.render(mFloorTile);
        modelBatch.end();
    }
}
