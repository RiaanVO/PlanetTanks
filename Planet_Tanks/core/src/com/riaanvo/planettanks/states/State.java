package com.riaanvo.planettanks.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.riaanvo.planettanks.managers.GameStateManager;

/**
 * Created by riaanvo on 8/5/17.
 */

public abstract class State {
    protected GameStateManager mGameStateManager;

    public State(){
        mGameStateManager = GameStateManager.get();
    }

    public abstract void update(float deltaTime);
    public abstract void render(SpriteBatch spriteBatch, ModelBatch modelBatch);
    public abstract void dispose();
}
