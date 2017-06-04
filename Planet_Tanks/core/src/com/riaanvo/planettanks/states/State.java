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
    protected boolean isLoaded;
    protected boolean waitForLoading = true;

    public State() {
        mGameStateManager = GameStateManager.get();
        mContentManager = ContentManager.get();
        isLoaded = false;
    }

    protected abstract void loaded();

    protected boolean isLoaded() {
        if (!isLoaded) {
            if (mContentManager.assetManagerUpdate()) {
                isLoaded = true;
                loaded();
                initialiseInput();
            } else {
                return false;
            }
        }
        return true;
    }

    public void Update(float deltaTime) {
        if (waitForLoading && !isLoaded) {
            if (!isLoaded()) return;
        }
        update(deltaTime);
    }

    protected abstract void update(float deltaTime);

    public void Render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        if (waitForLoading && !isLoaded) {
            if (!isLoaded()) return;
        }
        render(spriteBatch, modelBatch);
    }

    protected abstract void render(SpriteBatch spriteBatch, ModelBatch modelBatch);

    protected void setWaitForLoading(boolean shouldWait) {
        waitForLoading = shouldWait;
        if (!waitForLoading && !isLoaded) isLoaded();
    }

    public abstract void initialiseInput();

    public abstract void dispose();
}
