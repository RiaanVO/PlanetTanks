package com.riaanvo.planettanks.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.managers.ContentManager;

/**
 * Created by riaanvo on 12/5/17.
 */

public class TransitionState extends State {
    public enum  TransitionType {
      BLACK_FADE_REPLACE;
    };

    private ContentManager mContentManager;
    private Stage mStage;

    private State mPreviousState;
    private State mNextState;
    private TransitionType mType;

    private float mMaxTime;
    private float mTimer;

    private boolean isLoadeding;

    private Texture blackFadeTexture;

    public TransitionState(State previousState, State nextState, TransitionType type) {
        mContentManager = ContentManager.get();
        mStage = new Stage(new ScreenViewport());

        mPreviousState = previousState;
        mNextState = nextState;
        mType = type;
        isLoadeding = true;

        switch (mType){
            case BLACK_FADE_REPLACE: blackFadeReplaceInit(); break;
        }
    }

    @Override
    public void update(float deltaTime) {
        if(isLoadeding) return;

        mTimer += deltaTime;
        switch (mType){
            case BLACK_FADE_REPLACE: blackFadeReplaceUpdate(); break;
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        if(isLoadeding){
            if(mContentManager.assetManagerUpdate()){
                Loaded();
            }
            mPreviousState.render(spriteBatch, modelBatch);
            return;
        }

        switch (mType){
            case BLACK_FADE_REPLACE: blackFadeReplaceRender(spriteBatch, modelBatch); break;
        }
    }

    private void Loaded(){
        isLoadeding = false;

        switch (mType){
            case BLACK_FADE_REPLACE: blackFadeReplaceLoaded(); break;
        }
    }

    @Override
    public void dispose() {
        mStage.dispose();
    }

    private void blackFadeReplaceInit(){
        mTimer = 0;
        mMaxTime = 1;
        mContentManager.addTexture(Constants.BLACK_TEXTURE);
    }

    private void blackFadeReplaceUpdate(){
        if(mTimer > mMaxTime){
            mGameStateManager.pop(); //Remove this transition state
            mGameStateManager.setState(mNextState); //Replace the previous state with the new one
        }
    }

    private void blackFadeReplaceRender(SpriteBatch spriteBatch, ModelBatch modelBatch){
        float alpha;
        if(mTimer < mMaxTime / 2){
            mPreviousState.render(spriteBatch, modelBatch);
            alpha = mTimer / (mMaxTime / 2);
        } else {
            mNextState.render(spriteBatch, modelBatch);
            alpha = 1 - (mTimer - mMaxTime/2)/(mMaxTime/2);
        }
        spriteBatch.setColor(0, 0, 0, alpha);
        spriteBatch.begin();
        spriteBatch.draw(blackFadeTexture, 0, 0, mStage.getWidth(), mStage.getHeight());
        spriteBatch.end();
        spriteBatch.setColor(0, 0, 0, 1);
    }

    private void blackFadeReplaceLoaded(){
        blackFadeTexture = mContentManager.getTexture(Constants.BLACK_TEXTURE);
    }


}
