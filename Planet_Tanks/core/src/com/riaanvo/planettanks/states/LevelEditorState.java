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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.LevelFramework.GameLevel;
import com.riaanvo.planettanks.managers.LevelManager;

import java.util.LinkedList;

/**
 * This class creates the level editor screen. It extends functionality from the state superclass
 * This screen allows the player to design their own levels, test them and save them if they want.
 */

public class LevelEditorState extends State {
    private LevelManager mLevelManager;
    private Stage mStage;

    //Used to change what type of game object a tile will contain
    private ButtonGroup mTypeButtonGroup;
    private LinkedList<ImageTextButton> mTypeButtons;

    //Used to create the visual level map
    private int mLevelWidth;
    private int mLevelHeight;
    private LinkedList<ImageButton> mLevelMapButtons;
    private TextureRegionDrawable[] mTileImages;
    private int[][] mLevelMap;

    //Used to check if the level is a valid level
    private boolean mHasPlayer;
    private int mCurrentPlayerButtonIndex;

    private TextButton mSaveButton;
    private TextButton mPlayTestButton;

    public LevelEditorState() {
        mLevelManager = LevelManager.get();
        mHasPlayer = false;
        mLevelWidth = 12;
        mLevelHeight = 9;
    }

    @Override
    protected void loaded() {
        mStage = new Stage(new ScalingViewport(Scaling.stretch, Constants.VIRTUAL_SCREEN_WIDTH, Constants.VIRTUAL_SCREEN_HEIGHT));
        Skin skin = mContentManager.getSkin(Constants.SKIN_KEY);

        //Load the game object icons that will be displayed on the level map
        mTileImages = new TextureRegionDrawable[LevelManager.LevelMapTiles.values().length];
        mTileImages[LevelManager.LevelMapTiles.valueOf(LevelManager.LevelMapTiles.FLOOR.name()).ordinal()] =
                new TextureRegionDrawable(new TextureRegion(mContentManager.getTexture(Constants.FLOOR_TILE)));

        mTileImages[LevelManager.LevelMapTiles.valueOf(LevelManager.LevelMapTiles.WALL.name()).ordinal()] =
                new TextureRegionDrawable(new TextureRegion(mContentManager.getTexture(Constants.WALL_TILE)));

        mTileImages[LevelManager.LevelMapTiles.valueOf(LevelManager.LevelMapTiles.SPIKES.name()).ordinal()] =
                new TextureRegionDrawable(new TextureRegion(mContentManager.getTexture(Constants.SPIKES_TILE)));

        mTileImages[LevelManager.LevelMapTiles.valueOf(LevelManager.LevelMapTiles.PLAYER.name()).ordinal()] =
                new TextureRegionDrawable(new TextureRegion(mContentManager.getTexture(Constants.PLAYER_TILE)));

        mTileImages[LevelManager.LevelMapTiles.valueOf(LevelManager.LevelMapTiles.ENEMY.name()).ordinal()] =
                new TextureRegionDrawable(new TextureRegion(mContentManager.getTexture(Constants.ENEMY_TILE)));

        //Create a dialog that can be used to show the user a message
        final Dialog notificationDialogue = new Dialog("", skin);
        notificationDialogue.setFillParent(true);

        //Create the button used to dismiss the dialogue
        TextButton dialogConfirm = new TextButton("OK", skin);
        dialogConfirm.setTransform(true);
        dialogConfirm.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                notificationDialogue.hide();
            }
        });
        notificationDialogue.getButtonTable().add(dialogConfirm).pad(30).width(200).height(100);

        //Creat the label that will show the notification text
        final Label notificationMessage = new Label("Message", skin, Constants.TITLE_FONT);
        notificationDialogue.getContentTable().add(notificationMessage);

        //Create the menu buttons
        TextButton mainMenuButton = new TextButton("BACK", skin);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //close this screen
                mGameStateManager.pop();
            }
        });

        mPlayTestButton = new TextButton("PLAY", skin);
        mPlayTestButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TextButton button = (TextButton) event.getListenerActor();
                if (button != null) {
                    //Check if the button is disabled
                    if (button.isDisabled()) {
                        //Show a notification if the level is incomplete
                        notificationMessage.setText("Level doesn't meet requirements:\nA player and enemy are needed at least");
                        notificationDialogue.show(mStage);
                        return;
                    }
                    mLevelManager.setIsPlaytest(true);
                    mLevelManager.setLevelToLoad(editorToLevel());
                    mGameStateManager.push(new PlayState());
                }
            }
        });

        TextButton clearButton = new TextButton("CLEAR", skin);
        clearButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clearLevel();
                updateButtonsDisabled(isValidLevel());
            }
        });


        mSaveButton = new TextButton("SAVE", skin);
        mSaveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TextButton button = (TextButton) event.getListenerActor();
                if (button != null) {
                    if (button.isDisabled()) {
                        //Show a notification if the level is incomplete
                        notificationMessage.setText("Level doesn't meet requirements:\nA player and enemy are needed at least");
                        notificationDialogue.show(mStage);
                        return;
                    }
                    //test to see if the level was saved and display a message
                    if (!mLevelManager.addLevel(editorToLevel())) {
                        notificationMessage.setText("Level already exists");
                        notificationDialogue.show(mStage);
                    } else {
                        notificationMessage.setText("Level Saved");
                        notificationDialogue.show(mStage);
                    }

                }
            }
        });

        //Set up the button group used to control what type of game object to place
        mTypeButtonGroup = new ButtonGroup();
        mTypeButtonGroup.setMinCheckCount(1);
        mTypeButtonGroup.setMaxCheckCount(1);
        mTypeButtonGroup.setUncheckLast(true);

        //Create and add the type buttons
        mTypeButtons = new LinkedList<ImageTextButton>();
        for (int i = 0; i < LevelManager.LevelMapTiles.values().length; i++) {
            String s = LevelManager.LevelMapTiles.values()[i].name();
            ImageTextButton button = new ImageTextButton(s, skin, "radio");
            mTypeButtons.add(button);
            mTypeButtonGroup.add(button);
        }
        mTypeButtons.getFirst().setChecked(true);

        //Create the table to display the type buttons
        float typeButtonPad = 10f;
        Table typeButtonTable = new Table();
        typeButtonTable.setTransform(true);
        typeButtonTable.defaults().expandX().left();
        for (ImageTextButton button : mTypeButtons) {
            typeButtonTable.add(button).pad(typeButtonPad);
            typeButtonTable.row();
        }


        //Create a table to store an show the levelmap buttons
        float levelTileSize = 50f;
        float levelTilePad = 1f;
        Table levelMapTable = new Table();
        levelMapTable.setTransform(true);

        //Create the buttons list and the level map
        mLevelMapButtons = new LinkedList<ImageButton>();
        mLevelMap = new int[mLevelHeight][mLevelWidth];

        //Populate the buttons list and the level map
        for (int y = 0; y < mLevelHeight; y++) {
            for (int x = 0; x < mLevelWidth; x++) {
                //Set up the button style
                ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
                buttonStyle.up = skin.getDrawable("button-c");
                buttonStyle.down = skin.getDrawable("button-p");
                buttonStyle.over = skin.getDrawable("button-h");
                buttonStyle.disabled = skin.getDrawable("button-d");

                //Create an add the button
                ImageButton imgButton = new ImageButton(skin);
                imgButton.setStyle(buttonStyle);
                imgButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        ImageButton button = (ImageButton) event.getListenerActor();
                        if (button != null) {
                            if (button.isDisabled()) return;
                            //Set the image of the button and update the save and play buttons
                            setButtonType(button, LevelManager.LevelMapTiles.values()[mTypeButtonGroup.getCheckedIndex()]);
                            updateButtonsDisabled(isValidLevel());
                        }

                    }
                });
                mLevelMapButtons.add(imgButton);
                levelMapTable.add(imgButton).pad(levelTilePad).width(levelTileSize).height(levelTileSize);

                //Disable the buttons along the boarder as they have to be walls
                if (y == 0 || y == mLevelHeight - 1 || x == 0 || x == mLevelWidth - 1) {
                    imgButton.setDisabled(true);
                    setButtonType(imgButton, LevelManager.LevelMapTiles.WALL);
                } else {
                    setButtonType(imgButton, LevelManager.LevelMapTiles.FLOOR);
                }
            }
            levelMapTable.row();
        }

        //Update the save and play buttons disabled state
        updateButtonsDisabled(isValidLevel());

        //Create a options container and add the buttons
        float optionButtonsPad = 10f;
        float optionButtonsWidth = 100f;
        float optionButtonsHeight = 50f;
        Table optionButtonsContainer = new Table();
        optionButtonsContainer.setTransform(true);
        optionButtonsContainer.add(mainMenuButton).pad(optionButtonsPad).width(optionButtonsWidth).height(optionButtonsHeight);
        optionButtonsContainer.add(mPlayTestButton).pad(optionButtonsPad).width(optionButtonsWidth).height(optionButtonsHeight);
        optionButtonsContainer.add(mSaveButton).pad(optionButtonsPad).width(optionButtonsWidth).height(optionButtonsHeight);
        optionButtonsContainer.add(clearButton).pad(optionButtonsPad).width(optionButtonsWidth).height(optionButtonsHeight);


        //Todo use a scroll pane to store the level map

        //Create the editing table and add the types and level map
        Table levelEditingTable = new Table();
        levelEditingTable.setTransform(true);
        levelEditingTable.add(typeButtonTable).padRight(50);
        levelEditingTable.add(levelMapTable);

        //Set up the main table and add it to the scene
        Table mainRoot = new Table();
        mainRoot.align(Align.center);
        mainRoot.setBounds(0, 0, mStage.getWidth(), mStage.getHeight());
        mainRoot.padBottom(20f);
        mainRoot.add(optionButtonsContainer);
        mainRoot.row();
        mainRoot.add(levelEditingTable);

        mStage.addActor(mainRoot);
    }

    /**
     * Sets the buttons image and alters the value stored in the level map that corresponds to this
     * button
     * @param button button to be edited
     * @param type type of game object to be placed there
     */
    private void setButtonType(ImageButton button, LevelManager.LevelMapTiles type) {
        //Calculate the buttons index and the level map coordinates
        int buttonIndex = mLevelMapButtons.indexOf(button);
        int x = buttonIndex % mLevelWidth;
        int y = buttonIndex / mLevelWidth;
        int typeIndex = LevelManager.LevelMapTiles.valueOf(type.name()).ordinal();

        //Check if the button is going to be the player.
        if (type == LevelManager.LevelMapTiles.PLAYER) {
            handlePlayerPlacement(buttonIndex);
        } else {
            //Check if the change will remove the player
            if (getButtonType(button) == LevelManager.LevelMapTiles.PLAYER) {
                mHasPlayer = false;
            }
        }

        //Set the image to be shown on the button
        TextureRegionDrawable image = mTileImages[typeIndex];
        button.getStyle().imageUp = image;
        button.getStyle().imageDown = image;

        mLevelMap[y][x] = typeIndex;
    }

    /**
     * Checks to see if the level has a player and at least one enemy
     * @return if the level is a playable level
     */
    private boolean isValidLevel() {
        if (!mHasPlayer) return false;
        //Check if there are any enemies on the map
        int enemyCode = LevelManager.LevelMapTiles.valueOf(LevelManager.LevelMapTiles.ENEMY.name()).ordinal();
        for (int y = 0; y < mLevelHeight; y++) {
            for (int x = 0; x < mLevelWidth; x++) {
                if (mLevelMap[y][x] == enemyCode) return true;
            }
        }
        return false;
    }

    /**
     * Update the play and save buttons disabled state
     * @param isViable the state of the level
     */
    private void updateButtonsDisabled(boolean isViable) {
        mSaveButton.setDisabled(!isViable);
        mPlayTestButton.setDisabled(!isViable);
    }

    /**
     * Used to relocate the players' starting position when the player moves the player icon.
     * @param buttonIndex of the new players position
     */
    private void handlePlayerPlacement(int buttonIndex) {
        //Check if there is a player on the screen
        if (mHasPlayer) {
            //Reset the previous players location to a floor tile
            ImageButton button = mLevelMapButtons.get(mCurrentPlayerButtonIndex);
            int typeIndex = LevelManager.LevelMapTiles.valueOf(LevelManager.LevelMapTiles.FLOOR.name()).ordinal();
            TextureRegionDrawable image = mTileImages[typeIndex];
            button.getStyle().imageUp = image;
            button.getStyle().imageDown = image;

            //Set the previous location to a floor tile
            int x = mCurrentPlayerButtonIndex % mLevelWidth;
            int y = mCurrentPlayerButtonIndex / mLevelWidth;
            mLevelMap[y][x] = typeIndex;
        }

        //Update the player position tracking variables
        mHasPlayer = true;
        mCurrentPlayerButtonIndex = buttonIndex;
    }

    /**
     * Get the int of the level map type
     * @param type type to get the int of
     * @return the index of that type
     */
    private int levelMapIntOf(LevelManager.LevelMapTiles type) {
        return LevelManager.LevelMapTiles.valueOf(type.name()).ordinal();
    }

    /**
     * Gets the type of level map tile from a button
     * @param button
     * @return
     */
    private LevelManager.LevelMapTiles getButtonType(ImageButton button) {
        //Find the buttons coordinates on the level map
        int buttonIndex = mLevelMapButtons.indexOf(button);
        int x = buttonIndex % mLevelWidth;
        int y = buttonIndex / mLevelWidth;
        return LevelManager.LevelMapTiles.values()[mLevelMap[y][x]];
    }

    /**
     * Reset all the middle tiles to the floor type
     */
    private void clearLevel() {
        mHasPlayer = false;
        for (int y = 1; y < mLevelHeight - 1; y++) {
            for (int x = 1; x < mLevelWidth - 1; x++) {
                setButtonType(mLevelMapButtons.get(y * mLevelWidth + x), LevelManager.LevelMapTiles.FLOOR);
            }
        }
    }

    /**
     * Create a Game level object from the level map
     * @return
     */
    private GameLevel editorToLevel() {
        //Make a new level map with the current level map
        int[][] newLevelMap = new int[mLevelHeight][mLevelWidth];
        for (int y = 0; y < mLevelHeight; y++) {
            for (int x = 0; x < mLevelWidth; x++) {
                newLevelMap[y][x] = mLevelMap[y][x];
            }
        }

        return new GameLevel((mLevelManager.getNumLevels() + 1) + "", newLevelMap, true, true);
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
        Gdx.input.setInputProcessor(mStage);
    }

    @Override
    public void dispose() {
        //Set the play mode to normal
        mLevelManager.setIsPlaytest(false);
        mStage.dispose();
    }
}
