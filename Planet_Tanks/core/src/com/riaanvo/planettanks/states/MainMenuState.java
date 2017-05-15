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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.managers.ContentManager;

/**
 * Created by riaanvo on 9/5/17.
 */

public class MainMenuState extends State{

    private boolean isLoading;
    private ContentManager mContentManager;
    private Stage mStage;
    private Skin mSkin;
    private Table mTable;

    private Label mTitle;
    private TextButton mStartButton;
    private TextButton mLevelSelectButton;
    private TextButton mLevelEditorButton;
    private TextButton mQuitButton;
    private Image mBackgroundImage;

    public MainMenuState(){
        mContentManager = ContentManager.get();

        mContentManager.addSkin(Constants.SKIN_KEY);
        mContentManager.addTexture(Constants.MAIN_MENU_BACKGROUND);

        isLoading = true;
    }

    @Override
    public void update(float deltaTime) {
        if(!isLoaded()) return;
    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        if(!isLoaded()) return;
        mStage.draw();
    }

    @Override
    public void dispose() {
        mStage.dispose();
    }

    private boolean isLoaded(){
        if(isLoading){
            if(mContentManager.assetManagerUpdate()){
                loaded();
            } else {
                return false;
            }
        }
        return true;
    }

    private void loaded(){
        isLoading = false;

        mStage = new Stage(new ScreenViewport());

        mTable = new Table();
        mTable.setWidth(mStage.getWidth());
        mTable.align(Align.center);

        mTable.setPosition(0, Gdx.graphics.getHeight() / 2);

        mSkin = mContentManager.getSkin(Constants.SKIN_KEY);

        mTitle = new Label("Planet Tanks", mSkin);
        mTitle.setFontScale(4);

        mStartButton = new TextButton("New Game", mSkin);
        mStartButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGameStateManager.push(new TransitionState(MainMenuState.this, new PlayState(), TransitionState.TransitionType.BLACK_FADE_REPLACE));
            }
        });

        mLevelSelectButton = new TextButton("Level Select", mSkin);
        mLevelSelectButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Level select pressed");
            }
        });

        mLevelEditorButton = new TextButton("Level Editor", mSkin);
        mLevelEditorButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Level editor pressed");
            }
        });

        mQuitButton = new TextButton("Quit Game", mSkin);
        mQuitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        mTable.add(mTitle).spaceBottom(20);
        mTable.row();
        mTable.add(mStartButton).spaceBottom(10);
        mTable.row();
        mTable.add(mLevelSelectButton).spaceBottom(10);
        mTable.row();
        mTable.add(mLevelEditorButton).spaceBottom(10);
        mTable.row();
        mTable.add(mQuitButton);

        mBackgroundImage = new Image(mContentManager.getTexture(Constants.MAIN_MENU_BACKGROUND));
        mBackgroundImage.setPosition(0, 0);
        mBackgroundImage.setSize(mStage.getWidth(), mStage.getHeight());

        mStage.addActor(mBackgroundImage);
        mStage.addActor(mTable);
        Gdx.input.setInputProcessor(mStage);
    }
}