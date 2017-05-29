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
        BLACK_FADE_REPLACE,
        BLACK_FADE_ADD,
        BLACK_FADE_REMOVE
    }

    private Stage mStage;

    private State mPreviousState;
    private State mNextState;
    private TransitionType mType;

    private float mMaxTime;
    private float mTimer;

    private Texture blackFadeTexture;

    public TransitionState(State previousState, State nextState, TransitionType type) {
        mStage = new Stage(new ScreenViewport());

        mPreviousState = previousState;
        mNextState = nextState;
        mType = type;

        mTimer = 0;
        switch (mType){
            case BLACK_FADE_REPLACE: blackFadeReplaceInit(); break;
            case BLACK_FADE_ADD: blackFadeAddInit(); break;
            case BLACK_FADE_REMOVE: blackFadeRemoveInit(); break;
        }
    }

    @Override
    protected void update(float deltaTime) {
        mTimer += deltaTime;
        switch (mType){
            case BLACK_FADE_REPLACE: blackFadeReplaceUpdate(); break;
            case BLACK_FADE_ADD: blackFadeAddUpdate(); break;
            case BLACK_FADE_REMOVE: blackFadeRemoveUpdate(); break;
        }
    }

    @Override
    public void Render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        render(spriteBatch, modelBatch);
    }

    @Override
    protected void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        switch (mType){
            case BLACK_FADE_REPLACE: blackFadeReplaceRender(spriteBatch, modelBatch); break;
            case BLACK_FADE_ADD: blackFadeAddRender(spriteBatch, modelBatch); break;
            case BLACK_FADE_REMOVE: blackFadeRemoveRender(spriteBatch, modelBatch); break;
        }
    }

    @Override
    protected void loaded(){
        switch (mType){
            case BLACK_FADE_REPLACE: blackFadeReplaceLoaded(); break;
            case BLACK_FADE_ADD: blackFadeAddLoaded(); break;
            case BLACK_FADE_REMOVE: blackFadeRemoveLoaded(); break;
        }
    }

    @Override
    public void dispose() {
        mStage.dispose();
    }

    private void blackFadeReplaceInit(){
        mMaxTime = 1;
        mContentManager.loadTexture(Constants.BLACK_TEXTURE);
    }
    private void blackFadeReplaceLoaded(){
        blackFadeTexture = mContentManager.getTexture(Constants.BLACK_TEXTURE);
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
            mPreviousState.Render(spriteBatch, modelBatch);
            alpha = mTimer / (mMaxTime / 2);
        } else {
            mNextState.Render(spriteBatch, modelBatch);
            alpha = 1 - (mTimer - mMaxTime/2)/(mMaxTime/2);
        }
        if(blackFadeTexture == null) return;
        spriteBatch.setColor(0, 0, 0, alpha);
        spriteBatch.begin();
        spriteBatch.draw(blackFadeTexture, 0, 0, mStage.getWidth(), mStage.getHeight());
        spriteBatch.end();
        spriteBatch.setColor(0, 0, 0, 1);
    }


    private void blackFadeAddInit(){
        mMaxTime = 1;
        mContentManager.loadTexture(Constants.BLACK_TEXTURE);
    }
    private void blackFadeAddLoaded(){
        blackFadeTexture = mContentManager.getTexture(Constants.BLACK_TEXTURE);
    }
    private void blackFadeAddUpdate(){
        if(mTimer > mMaxTime){
            //mGameStateManager.pop(); //Remove this transition state
            mGameStateManager.setState(mNextState); //Replace the previous state with the new one
        }
    }
    private void blackFadeAddRender(SpriteBatch spriteBatch, ModelBatch modelBatch){
        float alpha;
        if(mTimer < mMaxTime / 2){
            mPreviousState.Render(spriteBatch, modelBatch);
            alpha = mTimer / (mMaxTime / 2);
        } else {
            mNextState.Render(spriteBatch, modelBatch);
            alpha = 1 - (mTimer - mMaxTime/2)/(mMaxTime/2);
        }
        if(blackFadeTexture == null) return;
        spriteBatch.setColor(0, 0, 0, alpha);
        spriteBatch.begin();
        spriteBatch.draw(blackFadeTexture, 0, 0, mStage.getWidth(), mStage.getHeight());
        spriteBatch.end();
        spriteBatch.setColor(0, 0, 0, 1);
    }



    private void blackFadeRemoveInit(){
        mMaxTime = 1;
        mContentManager.loadTexture(Constants.BLACK_TEXTURE);
    }
    private void blackFadeRemoveLoaded(){
        blackFadeTexture = mContentManager.getTexture(Constants.BLACK_TEXTURE);
    }
    private void blackFadeRemoveUpdate(){
        if(mTimer > mMaxTime){
            //mGameStateManager.pop(); //Remove this transition state
            mGameStateManager.setState(mNextState); //Replace the previous state with the new one
        }
    }
    private void blackFadeRemoveRender(SpriteBatch spriteBatch, ModelBatch modelBatch){
        float alpha;
        if(mTimer < mMaxTime / 2){
            mPreviousState.Render(spriteBatch, modelBatch);
            alpha = mTimer / (mMaxTime / 2);
        } else {
            mNextState.Render(spriteBatch, modelBatch);
            alpha = 1 - (mTimer - mMaxTime/2)/(mMaxTime/2);
        }
        if(blackFadeTexture == null) return;
        spriteBatch.setColor(0, 0, 0, alpha);
        spriteBatch.begin();
        spriteBatch.draw(blackFadeTexture, 0, 0, mStage.getWidth(), mStage.getHeight());
        spriteBatch.end();
        spriteBatch.setColor(0, 0, 0, 1);
    }
}
