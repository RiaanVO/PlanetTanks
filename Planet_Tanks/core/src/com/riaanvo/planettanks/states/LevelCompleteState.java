package com.riaanvo.planettanks.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.managers.LevelManager;

/**
 * Created by riaanvo on 29/5/17.
 */

public class LevelCompleteState extends State {

    private Stage mStage;
    private Skin mSkin;
    private Table mTable;

    private Label mTitle;
    private TextButton mNextLevelButton;
    private TextButton mMainMenuButton;
    private Texture blackFadeTexture;

    private float alpha;
    private State mPlayState;

    private boolean transitionedIn;
    private boolean transitionOut;
    private float fadeInTime;
    private float fadeInTimer;

    private LevelManager mLevelManager;

    public LevelCompleteState() {
        mContentManager.loadSkin(Constants.SKIN_KEY);
        mContentManager.loadTexture(Constants.BLACK_TEXTURE);
        mPlayState = mGameStateManager.getState(0);
        mLevelManager = LevelManager.get();
        alpha = 0.8f;
        transitionedIn = false;
        transitionOut = false;
        fadeInTime = 0.5f;
        fadeInTimer = 0f;
    }

    @Override
    protected void update(float deltaTime) {
        if(!transitionedIn){
            fadeInTimer += deltaTime;
            if(fadeInTimer >= fadeInTime){
                fadeInTimer = fadeInTime;
                transitionedIn = true;
            }
            if(mPlayState != null){
                mPlayState.Update(deltaTime);
            }
        } else {
            mStage.act(deltaTime);
            if(transitionOut){
                if(mPlayState != null){
                    mPlayState.Update(deltaTime);
                }
                fadeInTimer -= deltaTime;
                if(fadeInTimer < 0){
                    fadeInTimer = 0;
                    mGameStateManager.pop();
                }
            }
        }

    }

    @Override
    protected void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        if(mPlayState != null){
            mPlayState.render(spriteBatch, modelBatch);
        }
        float currentAlpha = alpha * (fadeInTimer / fadeInTime);
        if (blackFadeTexture == null) return;
        spriteBatch.setColor(0, 0, 0, currentAlpha);
        spriteBatch.begin();
        spriteBatch.draw(blackFadeTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();
        spriteBatch.setColor(0, 0, 0, 1);

        if(transitionedIn && !transitionOut) mStage.draw();
    }

    @Override
    protected void loaded() {
        blackFadeTexture = mContentManager.getTexture(Constants.BLACK_TEXTURE);

        mStage = new Stage(new ScalingViewport(Scaling.stretch, Constants.VIRTUAL_SCREEN_WIDTH, Constants.VIRTUAL_SCREEN_HEIGHT));
        mSkin = mContentManager.getSkin(Constants.SKIN_KEY);

        mTitle = new Label("LEVEL " + mLevelManager.getLevelName() + " COMPLETE!", mSkin, Constants.TITLE_FONT);
        mTitle.setFontScale(2);
        mTitle.setAlignment(Align.center);

            mNextLevelButton = new TextButton("NEXT LEVEL", mSkin);
            mNextLevelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    mLevelManager.loadNextLevel();
                    transitionOut = true;
                    //mGameStateManager.pop();
                }
            });


        mMainMenuButton = new TextButton("MAIN MENU", mSkin);
        mMainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGameStateManager.removeState(1);
                mGameStateManager.pop();
            }
        });


        Table mTable = new Table();
        mTable.setTransform(true);
        mTable.padBottom(20f);
        mTable.setBounds(0,0, mStage.getWidth(), mStage.getHeight());

        float buttonWidth = 180;
        float buttonHeight = 80;

        mTable.add(mTitle).pad(10f);
        mTable.row();
        if(mLevelManager.isAnotherLevel()) {
            mTable.add(mNextLevelButton).pad(10f).width(buttonWidth).height(buttonHeight);
            mTable.row();
        }
        mTable.add(mMainMenuButton).pad(10f).width(buttonWidth).height(buttonHeight);

        mStage.addActor(mTable);
    }

    @Override
    public void initialiseInput() {
        if(mStage == null) return;
        Gdx.input.setInputProcessor(mStage);
    }


    @Override
    public void dispose() {
        if(mStage == null) return;
        mStage.dispose();
    }
}
