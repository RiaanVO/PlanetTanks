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
import com.riaanvo.planettanks.states.State;

import java.util.LinkedList;

/**
 * This system acts as a manager that controls what state(screen) is currently being updated and
 * rendered.
 */

public class GameStateManager {
    private LinkedList<State> mStates = new LinkedList<State>();

    private static GameStateManager sGameStateManager;

    /**
     * Gets the current instance of the game state manager. Creates one if there isn't an instance
     *
     * @return the instance of the game state manager
     */
    public static GameStateManager get() {
        if (sGameStateManager == null) sGameStateManager = new GameStateManager();
        return sGameStateManager;
    }

    /**
     * Adds the state to the start of the list
     *
     * @param state the state that will be added to the start of the list
     */
    public void push(State state) {
        mStates.add(0, state);
    }

    /**
     * Removes the state at the start of the list if there are any states
     */
    public void pop() {
        if (mStates.size() == 0) return;
        mStates.get(0).dispose();
        mStates.remove(0);
        if (mStates.size() != 0) mStates.get(0).initialiseInput();
    }

    /**
     * Removes the current state at the start of the list and then adds the new state passed in at
     * the start of the list
     *
     * @param state the state that will be added to the start of the list
     */
    public void setState(State state) {
        pop();
        push(state);
    }

    /**
     * Call the update method on the state at the start of the list
     *
     * @param deltaTime the time difference between the last update call
     */
    public void update(float deltaTime) {
        if (mStates.size() == 0) return;
        mStates.get(0).Update(deltaTime);
    }

    /**
     * Call the render method on the state at the start of the list
     *
     * @param spriteBatch the sprite batch that will be used to render 2D graphics
     * @param modelBatch  the model batch that will be used to render 3D graphics
     */
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        if (mStates.size() == 0) return;
        mStates.get(0).Render(spriteBatch, modelBatch);
    }

    /**
     * Gets the state at that position in the list
     *
     * @param index position of the desired state
     * @return the state at that position
     */
    public State getState(int index) {
        if (index < mStates.size()) {
            return mStates.get(index);
        }
        return null;
    }

    /**
     * Removes the state at the index provided
     *
     * @param index position of the state to be removed
     */
    public void removeState(int index) {
        if (index < mStates.size()) {
            mStates.get(index).dispose();
            mStates.remove(index);
        }
    }

    /**
     * Gets the state at the start of the list
     *
     * @return the state at the start of the list
     */
    public State getCurrentState() {
        return mStates.getFirst();
    }

    /**
     * Remove all states from the list
     */
    public void dispose() {
        while (mStates.size() != 0) {
            pop();
        }
    }

}
