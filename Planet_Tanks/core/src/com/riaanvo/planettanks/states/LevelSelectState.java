package com.riaanvo.planettanks.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.LevelFramework.GameLevel;
import com.riaanvo.planettanks.managers.LevelManager;

/**
 * Created by riaanvo on 29/5/17.
 */

public class LevelSelectState extends State {

    private LevelManager mLevelManager;

    private Stage mStage;
    private Skin mSkin;

    private TextButton mainMenuButton;
    private Label mTitle;

    private TextButton leftArrow;
    private TextButton rightArrow;

    private TextButton[] levelButtons;

    private Label pageLabel;

    private int currentPage;
    private int maxNumPages;

    public LevelSelectState() {
        mLevelManager = LevelManager.get();
        mContentManager.loadSkin(Constants.SKIN_KEY);
        mContentManager.loadTexture(Constants.MAIN_MENU_BACKGROUND);
    }

    @Override
    protected void loaded() {
        maxNumPages = (mLevelManager.getNumLevels() / 8) + 1;
        currentPage = 1;

        mStage = new Stage(new ScalingViewport(Scaling.stretch, Constants.VIRTUAL_SCREEN_WIDTH, Constants.VIRTUAL_SCREEN_HEIGHT));
        mSkin = mContentManager.getSkin(Constants.SKIN_KEY);

        mTitle = new Label("SELECT LEVEL", mSkin, Constants.TITLE_FONT);
        mTitle.setFontScale(2);
        mTitle.setAlignment(Align.center);

        mainMenuButton = new TextButton("BACK", mSkin);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGameStateManager.pop();
            }
        });

        leftArrow = new TextButton("<", mSkin);
        leftArrow.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentPage --;
                if(currentPage < 1){
                    currentPage = 1;
                }
                setupPage(currentPage);
            }
        });

        rightArrow = new TextButton(">", mSkin);
        rightArrow.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentPage ++;
                if(currentPage > maxNumPages){
                    currentPage = maxNumPages;
                }
                setupPage(currentPage);
            }
        });

        levelButtons = new TextButton[8];
        for(int i = 0; i < levelButtons.length; i ++){
            levelButtons[i] = new TextButton("?", mSkin);
            levelButtons[i].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    TextButton button = (TextButton)event.getListenerActor();
                    if(button != null){
                        if(button.isDisabled()) return;
                        int index = Integer.parseInt(button.getText() + "") - 1;
                        startLevel(index);
                    }
                }
            });
        }

        pageLabel = new Label("", mSkin);
        pageLabel.setFontScale(2);

        Table levelsTable = new Table();
        levelsTable.setTransform(true);
        levelsTable.padBottom(10f);
        levelsTable.setBounds(0,0, mStage.getWidth(), mStage.getHeight());

        float buttonWidth = 100;
        float buttonHeight = 100;

        levelsTable.add(levelButtons[0]).pad(10f).width(buttonWidth).height(buttonHeight);
        levelsTable.add(levelButtons[1]).pad(10f).width(buttonWidth).height(buttonHeight);
        levelsTable.add(levelButtons[2]).pad(10f).width(buttonWidth).height(buttonHeight);
        levelsTable.add(levelButtons[3]).pad(10f).width(buttonWidth).height(buttonHeight);
        levelsTable.row();
        levelsTable.add(levelButtons[4]).pad(10f).width(buttonWidth).height(buttonHeight);
        levelsTable.add(levelButtons[5]).pad(10f).width(buttonWidth).height(buttonHeight);
        levelsTable.add(levelButtons[6]).pad(10f).width(buttonWidth).height(buttonHeight);
        levelsTable.add(levelButtons[7]).pad(10f).width(buttonWidth).height(buttonHeight);

        Table levelsContainer = new Table();
        levelsContainer.setTransform(true);

        leftArrow.setTransform(true);
        leftArrow.getLabel().setFontScale(2);
        levelsContainer.add(leftArrow).pad(10f).width(buttonWidth).height(buttonHeight);

        levelsContainer.add(levelsTable);

        rightArrow.setTransform(true);
        rightArrow.getLabel().setFontScale(2);
        levelsContainer.add(rightArrow).pad(10f).width(buttonWidth).height(buttonHeight);

        Table mainContainer = new Table();
        mainContainer.align(Align.center);
        mainContainer.padBottom(20f);
        mainContainer.setBounds(0, 0, mStage.getWidth(), mStage.getHeight());

        mainContainer.add(mTitle).padBottom(20f);
        mainContainer.row();
        mainContainer.add(levelsContainer).padBottom(20f);
        mainContainer.row();
        mainContainer.add(pageLabel).padBottom(20f);
        mainContainer.row();
        mainContainer.add(mainMenuButton).width(buttonWidth*2).height(buttonHeight/2);

        mStage.addActor(mainContainer);
        setupPage(1);
    }

    private void setupPage(int pageNumber){
        currentPage = pageNumber;

        int numLevels = mLevelManager.getNumLevels();
        int startingLevelIndex = 8 * (currentPage - 1);
        int endingLevelIndex = startingLevelIndex + 7;

        for(int i = startingLevelIndex; i <= endingLevelIndex; i ++){
            int buttonIndex = i - startingLevelIndex;
            if(i >= numLevels){
                levelButtons[buttonIndex].setVisible(false);
            } else {
                levelButtons[buttonIndex].setVisible(true);
                alterButton(levelButtons[buttonIndex], i + 1, mLevelManager.getLevel(i));
            }
        }

        leftArrow.setVisible(currentPage != 1);
        rightArrow.setVisible(currentPage != maxNumPages);

        pageLabel.setText("( " + currentPage + " / " + maxNumPages + " )");
    }

    private void alterButton(TextButton button, int levelNum, GameLevel level){
        button.setText(levelNum + "");
        if(level.isUserGenerated()){
            button.setColor(Color.BLUE);
        } else {
            button.setColor(Color.WHITE);
        }
        if(level.isUnlocked()){
            button.setDisabled(false);
        } else {
            button.setDisabled(true);
        }
    }

    private void startLevel(int levelIndex){
        mLevelManager.setLevelToLoad(levelIndex);
        mGameStateManager.setState(new PlayState());
    }

    @Override
    protected void update(float deltaTime) {
        mStage.act();
    }

    @Override
    protected void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        mStage.draw();
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
