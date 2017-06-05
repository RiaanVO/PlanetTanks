/*
 * Copyright (C) 2017 Riaan Van Onselen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 * This class creates the editor menu screen shown before the level editor. It extends functionality
 * from the state super class. Players can choose to create levels as well as delete or play the
 * selected level
 */

public class EditorMenuState extends State {
    private LevelManager mLevelManager;
    private Stage mStage;

    //Used for determining what levels to show
    private int mMaxNumPages;
    private int mCurrentPage;
    private int mNumLevelsPerPage;

    private LinkedList<GameLevel> mUserMadeLevels;
    private ButtonGroup mLevelButtonsGroup;
    private LinkedList<ImageTextButton> mLevelButtons;

    private Label mPageLabel;
    private TextButton mLeftArrow;
    private TextButton mRightArrow;


    private TextButton mPlayButton;
    private TextButton mDeleteButton;


    public EditorMenuState() {
        mLevelManager = LevelManager.get();
    }

    @Override
    protected void loaded() {
        mUserMadeLevels = mLevelManager.getUserMadeLevels();
        mStage = new Stage(new ScalingViewport(Scaling.stretch, Constants.VIRTUAL_SCREEN_WIDTH, Constants.VIRTUAL_SCREEN_HEIGHT));
        Skin skin = mContentManager.getSkin(Constants.SKIN_KEY);

        mNumLevelsPerPage = 4;
        mCurrentPage = 1;

        float buttonWidth = 200;
        float buttonHeight = 50;

        //Create the UI labels
        Label mTitle = new Label("SELECT LEVEL", skin, Constants.TITLE_FONT);
        mTitle.setFontScale(1.5f);
        mTitle.setAlignment(Align.center);

        Label levelsLabel = new Label("YOUR LEVELS:", skin);
        levelsLabel.setFontScale(2);

        mPageLabel = new Label("", skin);
        mPageLabel.setFontScale(2);

        //Create the UI buttons
        TextButton mainMenuButton = new TextButton("BACK", skin);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Close this state
                mGameStateManager.pop();
            }
        });

        TextButton newLevelButton = new TextButton("CREATE LEVEL", skin);
        newLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Start the level editor state
                mGameStateManager.push(new LevelEditorState());
            }
        });

        mPlayButton = new TextButton("PLAY", skin);
        mPlayButton.setDisabled(true);
        mPlayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Check if the button is disabled
                Button button = (Button) event.getListenerActor();
                if (button == null) return;
                if (button.isDisabled()) return;

                //Alter the state of the level manager
                mLevelManager.setIsPlaytest(true);
                int levelIndex = (mCurrentPage - 1) * mNumLevelsPerPage + mLevelButtonsGroup.getCheckedIndex();
                mLevelManager.setLevelToLoad(mUserMadeLevels.get(levelIndex));

                //Start the play state
                mGameStateManager.push(new PlayState());

                //Deactivate the checked button and reset the button states
                mLevelButtonsGroup.getChecked().setChecked(false);
                alterButtonStates();
            }
        });

        mDeleteButton = new TextButton("DELETE", skin);
        mDeleteButton.setDisabled(true);
        mDeleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Check if the button is disabled
                Button button = (Button) event.getListenerActor();
                if (button == null) return;
                if (button.isDisabled()) return;

                //Get the index of the level and remove it from the manager
                int levelIndex = (mCurrentPage - 1) * mNumLevelsPerPage + mLevelButtonsGroup.getCheckedIndex();
                mLevelManager.removeLevel(mUserMadeLevels.get(levelIndex));
                mUserMadeLevels.remove(levelIndex);

                //Adjust the number of pages to be shown based on the number of user levels
                if ((mCurrentPage - 1) * (mNumLevelsPerPage) >= mUserMadeLevels.size()) {
                    mCurrentPage--;
                    if (mCurrentPage < 1) mCurrentPage = 1;
                }
                setupPage(mCurrentPage);

                //Deactivate the checked button and reset the button states
                mLevelButtonsGroup.getChecked().setChecked(false);
                alterButtonStates();
            }
        });

        mLeftArrow = new TextButton("<", skin);
        mLeftArrow.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Change the page number but clamp it to 1
                mCurrentPage--;
                if (mCurrentPage < 1) {
                    mCurrentPage = 1;
                }
                setupPage(mCurrentPage);
            }
        });

        mRightArrow = new TextButton(">", skin);
        mRightArrow.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Change the page number but clamp it to the max number of pages
                mCurrentPage++;
                if (mCurrentPage > mMaxNumPages) {
                    mCurrentPage = mMaxNumPages;
                }
                setupPage(mCurrentPage);
            }
        });

        //Create the button group and set its variables
        mLevelButtonsGroup = new ButtonGroup();
        mLevelButtonsGroup.setMinCheckCount(0);
        mLevelButtonsGroup.setMaxCheckCount(1);
        mLevelButtonsGroup.setUncheckLast(true);

        //Create the list of buttons and populate it
        mLevelButtons = new LinkedList<ImageTextButton>();
        for (int i = 0; i < mNumLevelsPerPage; i++) {
            ImageTextButton button = new ImageTextButton("", skin, "radio");
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //Alter the states of the play and delete buttons whenever it is pressed
                    alterButtonStates();
                }
            });
            mLevelButtons.add(button);
            mLevelButtonsGroup.add(button);
        }

        //Set up the table for the level buttons
        Table levelsTable = new Table();
        levelsTable.setTransform(true);
        levelsTable.defaults().expandX().left();
        levelsTable.align(Align.center);
        levelsTable.add(levelsLabel).pad(10f);
        //Add the buttons to the table
        for (int i = 0; i < mLevelButtons.size(); i++) {
            levelsTable.row();
            levelsTable.add(mLevelButtons.get(i)).pad(2).width(buttonWidth).height(buttonHeight);
        }

        //Set up the table for the options buttons
        Table optionsContainer = new Table();
        optionsContainer.add(newLevelButton).pad(10f).width(buttonWidth).height(buttonHeight).padBottom(20f);
        optionsContainer.row();
        optionsContainer.add(mPlayButton).pad(10f).width(buttonWidth).height(buttonHeight).padBottom(20f);
        optionsContainer.row();
        optionsContainer.add(mDeleteButton).pad(10f).width(buttonWidth).height(buttonHeight);

        //Set up the table that will contain the middle UI elements
        Table middleContainer = new Table();
        middleContainer.setTransform(true);

        //Add the arrow buttons and the level buttons container
        mLeftArrow.setTransform(true);
        mLeftArrow.getLabel().setFontScale(2);
        middleContainer.add(mLeftArrow).pad(10f).width(buttonWidth / 2).height(buttonHeight * 2);

        middleContainer.add(levelsTable);

        mRightArrow.setTransform(true);
        mRightArrow.getLabel().setFontScale(2);
        middleContainer.add(mRightArrow).pad(10f).width(buttonWidth / 2).height(buttonHeight * 2);

        //Add the options buttons to the end of the middle container
        middleContainer.add(optionsContainer);


        //Set up the root table
        float mainTableSpacing = 5f;
        Table mainContainer = new Table();
        mainContainer.align(Align.center);
        mainContainer.setBounds(0, 0, mStage.getWidth(), mStage.getHeight());

        //Add the UI blocks to the root table
        mainContainer.add(mTitle).padBottom(mainTableSpacing);
        mainContainer.row();
        mainContainer.add(middleContainer).padBottom(mainTableSpacing);
        mainContainer.row();
        mainContainer.add(mPageLabel).padBottom(mainTableSpacing);
        mainContainer.row();
        mainContainer.add(mainMenuButton).width(buttonWidth).height(buttonHeight);

        //Add the root table to the scene and set up the page
        mStage.addActor(mainContainer);
        setupPage(1);
    }

    /**
     * Changes the UI elements to match the content on the page number
     *
     * @param pageNumber the page that will be displayed
     */
    private void setupPage(int pageNumber) {
        mCurrentPage = pageNumber;
        //Update the max number of pages
        mMaxNumPages = (mUserMadeLevels.size() / mNumLevelsPerPage) + 1;
        if (mUserMadeLevels.size() % mNumLevelsPerPage == 0 && mMaxNumPages != 1) mMaxNumPages--;

        //Set up the index for the starting and ending level to be displayed on the page
        int numLevels = mUserMadeLevels.size();
        int startingLevelIndex = mNumLevelsPerPage * (mCurrentPage - 1);
        int endingLevelIndex = startingLevelIndex + mNumLevelsPerPage - 1;

        //Increment through and change the text and visibility of the level buttons
        for (int i = startingLevelIndex; i <= endingLevelIndex; i++) {
            int buttonIndex = i - startingLevelIndex;
            if (i >= numLevels) {
                mLevelButtons.get(buttonIndex).setVisible(false);
            } else {
                mLevelButtons.get(buttonIndex).setVisible(true);
                mLevelButtons.get(buttonIndex).setText("Level " + mUserMadeLevels.get(i).getLevelName());
            }
        }

        //Change the visibility of the arrow buttons
        mLeftArrow.setVisible(mCurrentPage != 1);
        mRightArrow.setVisible(mCurrentPage != mMaxNumPages);

        //Set the page labels text
        mPageLabel.setText("( " + mCurrentPage + " / " + mMaxNumPages + " )");
    }

    /**
     * Checks if a level is selected
     *
     * @return boolean if a level is selected
     */
    private boolean levelSelected() {
        if (mLevelButtonsGroup.getAllChecked().size == 0) return false;
        return true;
    }

    /**
     * Updates the delete and play buttons disabled state
     */
    private void alterButtonStates() {
        mDeleteButton.setDisabled(!levelSelected());
        mPlayButton.setDisabled(!levelSelected());
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
        if (mStage == null) return;
        //Reset the input processor
        Gdx.input.setInputProcessor(mStage);
        //Get the user levels
        mUserMadeLevels = mLevelManager.getUserMadeLevels();
        setupPage(1);
    }

    @Override
    public void dispose() {
        if (mStage == null) return;
        mStage.dispose();
        //Set the play mode to normal
        mLevelManager.setIsPlaytest(false);
    }
}
