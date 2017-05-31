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

    public static GameStateManager get() {
        if (sGameStateManager == null) sGameStateManager = new GameStateManager();
        return sGameStateManager;
    }

    /**
     * Adds the state to the top of the stack
     *
     * @param state the state that will be added to the top of the stack
     */
    public void push(State state) {
        mStates.add(0, state);
    }

    /**
     * Removes the state at the top of the stack
     */
    public void pop() {
        if (mStates.size() == 0) return;
        mStates.get(0).dispose();
        mStates.remove(0);
        if(mStates.size() != 0) mStates.get(0).initialiseInput();
    }

    /**
     * Removes the current state at the top of the stack and then pushes the new state passed in onto the stack
     *
     * @param state the state that will be added to the top of the stack
     */
    public void setState(State state) {
        pop();
        push(state);
    }

    /**
     * Call the update method on the state on the top of the stack
     *
     * @param deltaTime the time difference between the last update call
     */
    public void update(float deltaTime) {
        if (mStates.size() == 0) return;
        mStates.get(0).Update(deltaTime);
    }

    /**
     * Call the render method on the state on the top of the stack
     *
     * @param spriteBatch the sprite batch that will be used to render 2D graphics
     * @param modelBatch  the model batch that will be used to render 3D graphics
     */
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        if (mStates.size() == 0) return;
        mStates.get(0).Render(spriteBatch, modelBatch);
    }

    public State getState(int index) {
        if (index < mStates.size()) {
            return mStates.get(index);
        }
        return null;
    }

    public void removeState(int index){
        if (index < mStates.size()) {
            mStates.get(index).dispose();
            mStates.remove(index);
        }
    }

    public void dispose() {
        while (mStates.size() != 0) {
            pop();
        }
    }

}
