package com.riaanvo.planettanks.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.LevelFramework.GameLevel;
import com.riaanvo.planettanks.managers.LevelManager;

import java.util.LinkedList;

/**
 * Created by riaanvo on 2/6/17.
 */

public class EditorMenuState extends State {
    private LevelManager mLevelManager;
    private Stage mStage;

    private int maxNumPages;
    private int currentPage;

    private Label pageLabel;
    private TextButton leftArrow;
    private TextButton rightArrow;

    private int numLevelsPerPage;
    private ButtonGroup levelButtonsGroup;
    private LinkedList<ImageTextButton> levelButtons;

    private TextButton playButton;
    private TextButton deleteButton;

    private LinkedList<GameLevel> userMadeLevels;

    public EditorMenuState(){
        mLevelManager = LevelManager.get();
    }

    @Override
    protected void loaded() {
        userMadeLevels = mLevelManager.getUserMadeLevels();

        numLevelsPerPage = 4;
        currentPage = 1;

        float buttonWidth = 200;
        float buttonHeight = 50;

        mStage = new Stage(new ScalingViewport(Scaling.stretch, Constants.VIRTUAL_SCREEN_WIDTH, Constants.VIRTUAL_SCREEN_HEIGHT));
        Skin skin = mContentManager.getSkin(Constants.SKIN_KEY);

        Label mTitle = new Label("SELECT LEVEL", skin, Constants.TITLE_FONT);
        mTitle.setFontScale(1.5f);
        mTitle.setAlignment(Align.center);

        Label levelsLabel = new Label("YOUR LEVELS:", skin);
        levelsLabel.setFontScale(2);

        pageLabel = new Label("", skin);
        pageLabel.setFontScale(2);

        TextButton mainMenuButton = new TextButton("BACK", skin);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGameStateManager.pop();
            }
        });

        TextButton newLevelButton = new TextButton("CREATE LEVEL", skin);
        newLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGameStateManager.push(new LevelEditorState());
            }
        });

        playButton = new TextButton("PLAY", skin);
        playButton.setDisabled(true);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Button button = (Button) event.getListenerActor();
                if(button == null) return;
                if(button.isDisabled()) return;

                mLevelManager.setIsPlaytest(true);
                int levelIndex = (currentPage - 1) * numLevelsPerPage + levelButtonsGroup.getCheckedIndex();
                mLevelManager.setLevelToLoad(userMadeLevels.get(levelIndex));
                mGameStateManager.push(new PlayState());

                levelButtonsGroup.getChecked().setChecked(false);
                alterButtonStates();
            }
        });

        deleteButton = new TextButton("DELETE", skin);
        deleteButton.setDisabled(true);
        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Button button = (Button) event.getListenerActor();
                if(button == null) return;
                if(button.isDisabled()) return;

                int levelIndex = (currentPage - 1) * numLevelsPerPage + levelButtonsGroup.getCheckedIndex();
                mLevelManager.removeLevel(userMadeLevels.get(levelIndex));
                userMadeLevels.remove(levelIndex);

                if((currentPage - 1) * (numLevelsPerPage) >= userMadeLevels.size()){
                    currentPage--;
                    if(currentPage < 1) currentPage = 1;
                }
                setupPage(currentPage);

                levelButtonsGroup.getChecked().setChecked(false);
                alterButtonStates();
            }
        });

        leftArrow = new TextButton("<", skin);
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

        rightArrow = new TextButton(">", skin);
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



        levelButtonsGroup = new ButtonGroup();
        levelButtonsGroup.setMinCheckCount(0);
        levelButtonsGroup.setMaxCheckCount(1);
        levelButtonsGroup.setUncheckLast(true);

        levelButtons = new LinkedList<ImageTextButton>();

        for(int i = 0; i < numLevelsPerPage; i ++){
            ImageTextButton button = new ImageTextButton("", skin, "radio");
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    alterButtonStates();
                }
            });
            levelButtons.add(button);
            levelButtonsGroup.add(button);
        }

        //            String s = "Level" + userMadeLevels.get(i).getLevelName();


        Table levelsTable = new Table();
        levelsTable.setTransform(true);
        levelsTable.defaults().expandX().left();
        levelsTable.align(Align.center);
        levelsTable.add(levelsLabel).pad(10f);
        for(int i = 0; i < levelButtons.size(); i ++){
            levelsTable.row();
            levelsTable.add(levelButtons.get(i)).pad(2).width(buttonWidth).height(buttonHeight);
        }



        Table optionsContainer = new Table();
        optionsContainer.add(newLevelButton).pad(10f).width(buttonWidth).height(buttonHeight).padBottom(20f);
        optionsContainer.row();
        optionsContainer.add(playButton).pad(10f).width(buttonWidth).height(buttonHeight).padBottom(20f);
        optionsContainer.row();
        optionsContainer.add(deleteButton).pad(10f).width(buttonWidth).height(buttonHeight);

        Table middleContainer = new Table();
        middleContainer.setTransform(true);

        leftArrow.setTransform(true);
        leftArrow.getLabel().setFontScale(2);
        middleContainer.add(leftArrow).pad(10f).width(buttonWidth/2).height(buttonHeight*2);

        middleContainer.add(levelsTable);

        rightArrow.setTransform(true);
        rightArrow.getLabel().setFontScale(2);
        middleContainer.add(rightArrow).pad(10f).width(buttonWidth/2).height(buttonHeight*2);

        middleContainer.add(optionsContainer);


        float mainTableSpacing = 5f;
        Table mainContainer = new Table();
        mainContainer.align(Align.center);
        mainContainer.setBounds(0, 0, mStage.getWidth(), mStage.getHeight());

        mainContainer.add(mTitle).padBottom(mainTableSpacing);
        mainContainer.row();
        mainContainer.add(middleContainer).padBottom(mainTableSpacing);
        mainContainer.row();
        mainContainer.add(pageLabel).padBottom(mainTableSpacing);
        mainContainer.row();
        mainContainer.add(mainMenuButton).width(buttonWidth).height(buttonHeight);

        mStage.addActor(mainContainer);
        setupPage(1);
    }

    private void setupPage(int pageNumber){
        currentPage = pageNumber;
        maxNumPages = (userMadeLevels.size() / numLevelsPerPage) + 1;
        if(userMadeLevels.size() % numLevelsPerPage == 0 && maxNumPages != 1) maxNumPages--;

        int numLevels = userMadeLevels.size();
        int startingLevelIndex = numLevelsPerPage * (currentPage - 1);
        int endingLevelIndex = startingLevelIndex + numLevelsPerPage - 1;

        for(int i = startingLevelIndex; i <= endingLevelIndex; i ++){
            int buttonIndex = i - startingLevelIndex;
            if(i >= numLevels){
                levelButtons.get(buttonIndex).setVisible(false);
            } else {
                levelButtons.get(buttonIndex).setVisible(true);
                levelButtons.get(buttonIndex).setText("Level " + userMadeLevels.get(i).getLevelName());
            }
        }

        leftArrow.setVisible(currentPage != 1);
        rightArrow.setVisible(currentPage != maxNumPages);

        pageLabel.setText("( " + currentPage + " / " + maxNumPages + " )");
    }

    private boolean levelSelected(){
        if(levelButtonsGroup.getAllChecked().size == 0) return false;
        return true;
    }

    private void alterButtonStates(){
        deleteButton.setDisabled(!levelSelected());
        playButton.setDisabled(!levelSelected());
    }

    @Override
    protected void update(float deltaTime) {
        mStage.act(deltaTime);
    }

    @Override
    protected void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        mStage.draw();
    }

    @Override
    public void initialiseInput() {
        if(mStage == null) return;
        Gdx.input.setInputProcessor(mStage);
        userMadeLevels = mLevelManager.getUserMadeLevels();
        setupPage(1);
    }

    @Override
    public void dispose() {
        if(mStage == null) return;
        mStage.dispose();
        mLevelManager.setIsPlaytest(false);
    }
}
