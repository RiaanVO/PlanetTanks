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

    public State(){
        mGameStateManager = GameStateManager.get();
        mContentManager = ContentManager.get();
        isLoaded = false;
    }

    public void Update(float deltaTime){
        if(waitForLoading && !isLoaded){
            if(!isLoaded()) return;
        }
        update(deltaTime);
    }

    protected abstract void update(float deltaTime);

    public void Render(SpriteBatch spriteBatch, ModelBatch modelBatch){
        if(waitForLoading && !isLoaded){
            if(!isLoaded()) return;
        }
        render(spriteBatch, modelBatch);
    }

    protected abstract void render(SpriteBatch spriteBatch, ModelBatch modelBatch);

    protected abstract void loaded();

    protected boolean isLoaded(){
        if(!isLoaded){
            if(mContentManager.assetManagerUpdate()){
                isLoaded = true;
                loaded();
            } else {
                return false;
            }
        }
        return true;
    }

    protected void setWaitForLoading(boolean shouldWait){
        waitForLoading = shouldWait;
        if(!waitForLoading && !isLoaded) isLoaded();
    }

    public abstract void dispose();
}
