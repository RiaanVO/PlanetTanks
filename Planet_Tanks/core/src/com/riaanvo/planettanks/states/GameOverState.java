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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.managers.LevelManager;

/**
 * Created by riaanvo on 29/5/17.
 */

public class GameOverState extends State {

    private Stage mStage;
    private Skin mSkin;
    private Table mTable;

    private Label mTitle;
    private TextButton mReplayButton;
    private TextButton mMainMenuButton;
    private Texture blackFadeTexture;

    private float alpha;
    private State mPlayState;

    private boolean transitionedIn;
    private float fadeInTime;
    private float fadeInTimer;

    public GameOverState() {
        mContentManager.loadSkin(Constants.SKIN_KEY);
        mContentManager.loadTexture(Constants.BLACK_TEXTURE);
        mPlayState = mGameStateManager.getState(0);
        alpha = 0.8f;
        transitionedIn = false;
        fadeInTime = 0.5f;
        fadeInTimer = 0f;
    }

    @Override
    protected void update(float deltaTime) {
        if(mPlayState != null){
            mPlayState.Update(deltaTime);
        }

        if(!transitionedIn){
            fadeInTimer += deltaTime;
            if(fadeInTimer >= fadeInTime){
                fadeInTimer = fadeInTime;
                transitionedIn = true;
            }
        } else {
            mStage.act(deltaTime);
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
        spriteBatch.draw(blackFadeTexture, 0, 0, mStage.getWidth(), mStage.getHeight());
        spriteBatch.end();
        spriteBatch.setColor(0, 0, 0, 1);

        if(transitionedIn) mStage.draw();
    }

    @Override
    protected void loaded() {
        mStage = new Stage(new ScreenViewport());

        mTable = new Table();
        mTable.setWidth(mStage.getWidth());
        mTable.align(Align.center);

        mTable.setPosition(0, Gdx.graphics.getHeight() / 2);

        mSkin = mContentManager.getSkin(Constants.SKIN_KEY);

        mTitle = new Label("Game Over", mSkin);
        mTitle.setFontScale(4);

        mReplayButton = new TextButton("Restart", mSkin);
        mReplayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGameStateManager.push(new TransitionState(null, TransitionState.TransitionType.BLACK_FADE_REMOVE));
                LevelManager.get().RestartLevel();
            }
        });

        mMainMenuButton = new TextButton("Main Menu", mSkin);
        mMainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGameStateManager.removeState(1);
                mGameStateManager.push(new TransitionState(null, TransitionState.TransitionType.BLACK_FADE_REMOVE));
            }
        });

        mTable.add(mTitle).spaceBottom(20);
        mTable.row();
        mTable.add(mReplayButton).spaceBottom(10);
        mTable.row();
        mTable.add(mMainMenuButton).spaceBottom(10);

        blackFadeTexture = mContentManager.getTexture(Constants.BLACK_TEXTURE);

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
