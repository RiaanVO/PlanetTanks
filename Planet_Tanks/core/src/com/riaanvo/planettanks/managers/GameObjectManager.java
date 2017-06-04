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

package com.riaanvo.planettanks.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.riaanvo.planettanks.GameObjects.GameObject;

import java.util.LinkedList;

/**
 * Created by riaanvo on 23/5/17.
 */

public class GameObjectManager {
    private static GameObjectManager sGameObjectManager;

    public static GameObjectManager get() {
        if (sGameObjectManager == null) sGameObjectManager = new GameObjectManager();
        return sGameObjectManager;
    }

    private LinkedList<GameObject> mGameObjects = new LinkedList<GameObject>();
    private LinkedList<GameObject> mNewGameObjects = new LinkedList<GameObject>();
    private LinkedList<GameObject> mDeadGameObjects = new LinkedList<GameObject>();

    public void update(float deltaTime) {
        mGameObjects.addAll(mNewGameObjects);
        mNewGameObjects.clear();

        for (GameObject gameObject : mGameObjects) {
            gameObject.update(deltaTime);
        }

        mGameObjects.removeAll(mDeadGameObjects);
        mDeadGameObjects.clear();
    }

    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        for (GameObject gameObject : mGameObjects) {
            if (gameObject.isVisible()) {
                gameObject.render(spriteBatch, modelBatch);
            }
        }
    }

    public void addGameObject(GameObject gameObject) {
        mNewGameObjects.add(gameObject);
    }

    public void removeGameObject(GameObject gameObject) {
        mDeadGameObjects.add(gameObject);
    }

    public void clearGameObjects() {
        mGameObjects.clear();
        mNewGameObjects.clear();
        mDeadGameObjects.clear();
    }
}
