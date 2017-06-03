package com.riaanvo.planettanks.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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

    private Label mTitle;
    private TextButton mPlayButton;
    private TextButton mLevelEditorButton;
    private TextButton mQuitButton;
    private Image mBackgroundImage;

    public MainMenuState() {

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
        mSkin = mContentManager.getSkin(Constants.SKIN_KEY);


        mBackgroundImage = new Image(mContentManager.getTexture(Constants.MAIN_MENU_BACKGROUND));
        mBackgroundImage.setPosition(0, 0);
        mBackgroundImage.setSize(mStage.getWidth(), mStage.getHeight());
        mStage.addActor(mBackgroundImage);

        mTitle = new Label("PLANET TANKS", mSkin, "title");
        mTitle.setFontScale(2);
        mTitle.setAlignment(Align.center);


        mPlayButton = new TextButton("PLAY", mSkin);
        mPlayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGameStateManager.push(new LevelSelectState());
            }
        });

        mLevelEditorButton = new TextButton("LEVEL EDITOR", mSkin);
        mLevelEditorButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGameStateManager.push(new EditorMenuState());
                //mGameStateManager.push(new LevelEditorState());
            }
        });

        mQuitButton = new TextButton("QUIT GAME", mSkin);
        mQuitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
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
        mTable.add(mPlayButton).pad(10f).width(buttonWidth).height(buttonHeight);
        mTable.row();
        mTable.add(mLevelEditorButton).pad(10f).width(buttonWidth).height(buttonHeight);
        mTable.row();
        mTable.add(mQuitButton).pad(10f).width(buttonWidth).height(buttonHeight);

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
