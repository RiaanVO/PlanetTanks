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
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.managers.LevelManager;

/**
 * Created by riaanvo on 1/6/17.
 */

public class PauseState extends State{
    private Stage mStage;
    private Skin mSkin;

    private Label mTitle;
    private TextButton mResumeButton;
    private TextButton mMainMenuButton;
    private Texture blackFadeTexture;

    private float alpha;
    private State mPlayState;

    private boolean transitionedIn;
    private boolean transitionOut;
    private float fadeInTime;
    private float fadeInTimer;

    public PauseState() {
        mPlayState = mGameStateManager.getState(0);
        alpha = 0.8f;
        transitionedIn = false;
        transitionOut = false;
        fadeInTime = 0.2f;
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
        } else {
            mStage.act(deltaTime);
            if(transitionOut){
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

        mTitle = new Label("PAUSED", mSkin, "title");
        mTitle.setFontScale(2);
        mTitle.setAlignment(Align.center);

        mResumeButton = new TextButton("RESUME", mSkin);
        mResumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                transitionOut = true;
            }
        });

        mMainMenuButton = new TextButton("QUIT", mSkin);
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
        mTable.add(mResumeButton).pad(10f).width(buttonWidth).height(buttonHeight);
        mTable.row();
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
