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
 * This class manages the list of game objects that will be rendered and updated each frame. It is
 * accessible from anywhere in the game through the use of the singleton pattern.
 */

public class GameObjectManager {
    private static GameObjectManager sGameObjectManager;

    /**
     * Gets the current instance of the game object manager. Creates one if there isn't an instance
     *
     * @return the instance of the game object manager
     */
    public static GameObjectManager get() {
        if (sGameObjectManager == null) sGameObjectManager = new GameObjectManager();
        return sGameObjectManager;
    }

    //Main list of game objects
    private LinkedList<GameObject> mGameObjects = new LinkedList<GameObject>();

    //Lists to manage adding and removeing game objects from the main list
    private LinkedList<GameObject> mNewGameObjects = new LinkedList<GameObject>();
    private LinkedList<GameObject> mDeadGameObjects = new LinkedList<GameObject>();

    /**
     * Adds all new game objects to the main list. Calls update of all game objects. Removes game
     * objects that need to be removed after update.
     *
     * @param deltaTime the time since the last update call
     */
    public void update(float deltaTime) {
        mGameObjects.addAll(mNewGameObjects);
        mNewGameObjects.clear();

        for (GameObject gameObject : mGameObjects) {
            gameObject.update(deltaTime);
        }

        mGameObjects.removeAll(mDeadGameObjects);
        mDeadGameObjects.clear();
    }

    /**
     * Renders each game object in the main object list that is visible by the camera.
     *
     * @param spriteBatch used to render 2D images
     * @param modelBatch  used to render 3D models
     */
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        for (GameObject gameObject : mGameObjects) {
            if (gameObject.isVisible()) {
                gameObject.render(spriteBatch, modelBatch);
            }
        }
    }

    /**
     * Adds a game object to the new game objects list. Will be added to the main list on next update
     *
     * @param gameObject that will be added
     */
    public void addGameObject(GameObject gameObject) {
        mNewGameObjects.add(gameObject);
    }

    /**
     * Adds a game object to the dead game objects list. Will be removed from the main list after
     * all game objects have been updated
     *
     * @param gameObject that will be removed
     */
    public void removeGameObject(GameObject gameObject) {
        mDeadGameObjects.add(gameObject);
    }

    /**
     * Clears all lists of game objects
     */
    public void clearGameObjects() {
        mGameObjects.clear();
        mNewGameObjects.clear();
        mDeadGameObjects.clear();
    }
}
