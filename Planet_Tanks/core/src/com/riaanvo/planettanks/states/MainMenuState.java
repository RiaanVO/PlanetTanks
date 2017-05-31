package com.riaanvo.planettanks.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.riaanvo.planettanks.Constants;

/**
 * Created by riaanvo on 9/5/17.
 */

public class MainMenuState extends State {

    private Stage mStage;
    private Skin mSkin;
    private Table mTable;

    private Label mTitle;
    private TextButton mPlayButton;
    private TextButton mLevelEditorButton;
    private TextButton mQuitButton;
    private Image mBackgroundImage;

    public MainMenuState() {
        mContentManager.loadSkin(Constants.SKIN_KEY);
        mContentManager.loadTexture(Constants.MAIN_MENU_BACKGROUND);
    }

    @Override
    protected void update(float deltaTime) {
    }

    @Override
    protected void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        mStage.draw();
    }

    @Override
    protected void loaded() {
        mStage = new Stage(new ScalingViewport(Scaling.stretch, Constants.VIRTUAL_SCREEN_WIDTH, Constants.VIRTUAL_SCREEN_HEIGHT));

        mTable = new Table();
        mTable.debug();
        mTable.setFillParent(true);
        mTable.align(Align.center);
        mTable.setPosition(0, 0);

        mSkin = mContentManager.getSkin(Constants.SKIN_KEY);

        mTitle = new Label("Planet Tanks", mSkin);
        mTitle.setFontScale(4);

        mPlayButton = new TextButton("Play", mSkin);
        mPlayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //mGameStateManager.push(new TransitionState(new PlayState(), TransitionState.TransitionType.BLACK_FADE_ADD));
                mGameStateManager.push(new LevelSelectState());
                //mGameStateManager.push(new PlayState());
            }
        });

        mLevelEditorButton = new TextButton("Level Editor", mSkin);
        mLevelEditorButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGameStateManager.push(new LevelEditorState());
            }
        });

        mQuitButton = new TextButton("Quit", mSkin);
        mQuitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        mTable.add(mTitle).spaceBottom(20);
        mTable.row();
        mTable.add(mPlayButton).spaceBottom(10);
        mTable.row();
        mTable.add(mLevelEditorButton).spaceBottom(10);
        mTable.row();
        mTable.add(mQuitButton);

        mBackgroundImage = new Image(mContentManager.getTexture(Constants.MAIN_MENU_BACKGROUND));
        mBackgroundImage.setPosition(0, 0);
        mBackgroundImage.setSize(mStage.getWidth(), mStage.getHeight());
        mStage.addActor(mBackgroundImage);

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
