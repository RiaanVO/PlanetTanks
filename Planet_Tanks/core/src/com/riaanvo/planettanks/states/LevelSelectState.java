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

    private VerticalGroup mainbranch;
    private HorizontalGroup topActions;
    private HorizontalGroup midActions;
    private Table levelsContainer;

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

        mainbranch = new VerticalGroup();
        topActions = new HorizontalGroup();
        midActions = new HorizontalGroup();
        levelsContainer = new Table();

        mTitle = new Label("Select Level", mSkin);

        mainMenuButton = new TextButton("Main Menu", mSkin);
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
                        if(button.isDisabled()) return;;
                        int index = Integer.parseInt(button.getText() + "") - 1;
                        startLevel(index);
                    }
                }
            });
        }
        pageLabel = new Label("pageNumbers", mSkin);

        mainbranch.setFillParent(true);
        mainbranch.align(Align.center);

        topActions.addActor(mainMenuButton);
        topActions.addActor(mTitle);
        mainbranch.addActor(topActions);

        midActions.addActor(leftArrow);

        levelsContainer.add(levelButtons[0]);
        levelsContainer.add(levelButtons[1]);
        levelsContainer.add(levelButtons[2]);
        levelsContainer.add(levelButtons[3]);
        levelsContainer.row();
        levelsContainer.add(levelButtons[4]);
        levelsContainer.add(levelButtons[5]);
        levelsContainer.add(levelButtons[6]);
        levelsContainer.add(levelButtons[7]);

        midActions.addActor(levelsContainer);
        midActions.addActor(rightArrow);

        mainbranch.addActor(midActions);

        mainbranch.addActor(pageLabel);

        mStage.addActor(mainbranch);
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
        if(level.isUnlocked()){
            if(level.isUserGenerated()){
                button.setColor(Color.BLUE);
            } else {
                button.setColor(Color.GREEN);
            }
            button.setDisabled(false);
        } else {
            button.setColor(Color.GRAY);
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
