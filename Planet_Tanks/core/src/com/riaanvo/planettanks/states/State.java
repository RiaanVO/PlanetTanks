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

package com.riaanvo.planettanks.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.riaanvo.planettanks.managers.ContentManager;
import com.riaanvo.planettanks.managers.GameStateManager;

/**
 * Created by riaanvo on 8/5/17.
 */

public abstract class State {
    protected GameStateManager mGameStateManager;
    protected ContentManager mContentManager;

    //used to prevent drawing and updating based on loading content
    protected boolean mIsLoaded;
    protected boolean mWaitForLoading = true;

    public State() {
        mGameStateManager = GameStateManager.get();
        mContentManager = ContentManager.get();
        mIsLoaded = false;
    }

    /**
     * Called when loading is done. Used to set up any game elements that required assets to be loaded
     */
    protected abstract void loaded();

    /**
     * Checks if the asset manager has finished loading and calls loaded and initialise input
     * once loading is complete
     *
     * @return boolean if the content is loaded
     */
    protected boolean isLoaded() {
        if (!mIsLoaded) {
            if (mContentManager.assetManagerUpdate()) {
                mIsLoaded = true;
                loaded();
                initialiseInput();
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Update the state depending on loading. Is called by the Game State Manager
     *
     * @param deltaTime the time since last update
     */
    public void Update(float deltaTime) {
        //Check if the state should wait for loading and is loaded
        if (mWaitForLoading && !mIsLoaded) {
            if (!isLoaded()) return;
        }
        update(deltaTime);
    }

    /**
     * The update method that will update all a states components
     *
     * @param deltaTime the time since last update
     */
    protected abstract void update(float deltaTime);

    /**
     * Render the state depending on loading. Is called by the Game State Manager
     *
     * @param spriteBatch used to render 2D images
     * @param modelBatch  used to render 3D models
     */
    public void Render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        //Check if the state should wait for loading and is loaded
        if (mWaitForLoading && !mIsLoaded) {
            if (!isLoaded()) return;
        }
        render(spriteBatch, modelBatch);
    }

    /**
     * The render method that will render all the states components
     *
     * @param spriteBatch use to render 2D images
     * @param modelBatch  used to render 3D models
     */
    protected abstract void render(SpriteBatch spriteBatch, ModelBatch modelBatch);

    /**
     * Set the wait for loading field
     *
     * @param shouldWait boolean state to be set
     */
    protected void setWaitForLoading(boolean shouldWait) {
        mWaitForLoading = shouldWait;
        //If not going to wait, call the loaded method
        if (!mWaitForLoading && !mIsLoaded) isLoaded();
    }

    /**
     * Used to re assign the input processor of game engine
     */
    public abstract void initialiseInput();

    /**
     * Used to dispose of any graphics and specific state components
     */
    public abstract void dispose();
}
