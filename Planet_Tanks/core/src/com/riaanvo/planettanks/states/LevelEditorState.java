package com.riaanvo.planettanks.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.managers.LevelManager;

/**
 * Created by riaanvo on 30/5/17.
 */

public class LevelEditorState extends State {
    private LevelManager mLevelManager;

    private Stage mStage;
    private Skin mSkin;

    private VerticalGroup mainRoot;
    private HorizontalGroup optionButtonsContainer;
    private HorizontalGroup levelEditorContainer;
    private VerticalGroup tileTypeButtonsContainer;
    private Table levelMapTable;


    private TextButton mainMenuButton;
    private TextButton playTestButton;
    private TextButton clearButton;
    private TextButton saveButton;

    private ButtonGroup typeButtonGroup;
    private TextButton[] typeButtons;

    private TextButton[][] levelMapButtons;
    private int levelWidth;
    private int levelHeight;



    public LevelEditorState(){
        mLevelManager = LevelManager.get();
        mContentManager.loadSkin(Constants.SKIN_KEY);
        mContentManager.loadTexture(Constants.MAIN_MENU_BACKGROUND);
    }

    @Override
    protected void loaded() {
        mStage = new Stage(new ScalingViewport(Scaling.stretch, Constants.VIRTUAL_SCREEN_WIDTH, Constants.VIRTUAL_SCREEN_HEIGHT));
        mSkin = mContentManager.getSkin(Constants.SKIN_KEY);

        levelWidth = 10;
        levelHeight = 7;

        mainMenuButton = new TextButton("BACK", mSkin);
        playTestButton = new TextButton("PLAY", mSkin);
        clearButton = new TextButton("CLEAR", mSkin);
        saveButton = new TextButton("SAVE", mSkin);


        typeButtonGroup = new ButtonGroup();
        typeButtonGroup.setMinCheckCount(1);
        typeButtonGroup.setMaxCheckCount(1);
        typeButtonGroup.setUncheckLast(true);

        typeButtons = new TextButton[LevelManager.LevelMapTiles.values().length];
        for(int i = 0; i < typeButtons.length; i ++){
            String s = LevelManager.LevelMapTiles.values()[i].name();
            typeButtons[i] = new TextButton(s, mSkin);
            typeButtonGroup.add(typeButtons[i]);
        }

        levelMapTable = new Table();
        levelMapButtons = new TextButton[levelWidth][levelHeight];
        for(int y = 0; y < levelHeight; y++){
            for(int x = 0; x < levelWidth; x++){
                levelMapButtons[x][y] = new TextButton(y+"", mSkin);
                levelMapTable.add(levelMapButtons[x][y]);
            }
            levelMapTable.row();
        }


        mainRoot = new VerticalGroup();
        mainRoot.align(Align.topLeft);
        mainRoot.setFillParent(true);

        optionButtonsContainer = new HorizontalGroup();
        levelEditorContainer = new HorizontalGroup();
        tileTypeButtonsContainer = new VerticalGroup();

        mainRoot.addActor(optionButtonsContainer);
        mainRoot.addActor(levelEditorContainer);

        optionButtonsContainer.addActor(mainMenuButton);
        optionButtonsContainer.addActor(playTestButton);
        optionButtonsContainer.addActor(clearButton);
        optionButtonsContainer.addActor(saveButton);

        for(TextButton button : typeButtons){
            tileTypeButtonsContainer.addActor(button);
        }
        levelEditorContainer.addActor(tileTypeButtonsContainer);

        levelEditorContainer.addActor(levelMapTable);

        mainRoot.debug();
        optionButtonsContainer.debug();
        levelEditorContainer.debug();
        tileTypeButtonsContainer.debug();
        levelMapTable.debug();

        mStage.addActor(mainRoot);
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
    }

    @Override
    public void dispose() {
        mStage.dispose();
    }
}
