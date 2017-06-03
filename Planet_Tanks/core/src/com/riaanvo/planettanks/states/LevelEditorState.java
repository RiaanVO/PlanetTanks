package com.riaanvo.planettanks.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.LevelFramework.GameLevel;
import com.riaanvo.planettanks.managers.LevelManager;

import java.util.LinkedList;

/**
 * Created by riaanvo on 30/5/17.
 */

public class LevelEditorState extends State {
    private LevelManager mLevelManager;
    private Stage mStage;

    private ButtonGroup typeButtonGroup;
    private LinkedList<ImageTextButton> typeButtons;

    private int levelWidth;
    private int levelHeight;
    private LinkedList<ImageButton> levelMapButtons;
    private TextureRegionDrawable[] tileImages;
    private int[][] levelMap;

    private boolean hasPlayer;
    private int currentPlayerButtonIndex;

    private TextButton saveButton;
    private TextButton playTestButton;

    private boolean levelEdited;

    public LevelEditorState(){
        mLevelManager = LevelManager.get();

        hasPlayer = false;
        levelEdited = false;
        levelWidth = 12;
        levelHeight = 9;
    }

    @Override
    protected void loaded() {
        mStage = new Stage(new ScalingViewport(Scaling.stretch, Constants.VIRTUAL_SCREEN_WIDTH, Constants.VIRTUAL_SCREEN_HEIGHT));
        Skin skin = mContentManager.getSkin(Constants.SKIN_KEY);

        tileImages = new TextureRegionDrawable[LevelManager.LevelMapTiles.values().length];


        tileImages[LevelManager.LevelMapTiles.valueOf(LevelManager.LevelMapTiles.FLOOR.name()).ordinal()] =
                new TextureRegionDrawable(new TextureRegion(mContentManager.getTexture(Constants.FLOOR_TILE)));

        tileImages[LevelManager.LevelMapTiles.valueOf(LevelManager.LevelMapTiles.WALL.name()).ordinal()] =
                new TextureRegionDrawable(new TextureRegion(mContentManager.getTexture(Constants.WALL_TILE)));

        tileImages[LevelManager.LevelMapTiles.valueOf(LevelManager.LevelMapTiles.SPIKES.name()).ordinal()] =
                new TextureRegionDrawable(new TextureRegion(mContentManager.getTexture(Constants.SPIKES_TILE)));

        tileImages[LevelManager.LevelMapTiles.valueOf(LevelManager.LevelMapTiles.PLAYER.name()).ordinal()] =
                new TextureRegionDrawable(new TextureRegion(mContentManager.getTexture(Constants.PLAYER_TILE)));

        tileImages[LevelManager.LevelMapTiles.valueOf(LevelManager.LevelMapTiles.ENEMY.name()).ordinal()] =
                new TextureRegionDrawable(new TextureRegion(mContentManager.getTexture(Constants.ENEMY_TILE)));


        final Dialog errorDialog = new Dialog("", skin);
        errorDialog.setFillParent(true);

        TextButton dialogConfirm = new TextButton("OK", skin);
        dialogConfirm.setTransform(true);
        dialogConfirm.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                errorDialog.hide();
            }
        });
        errorDialog.getButtonTable().add(dialogConfirm).pad(30).width(200).height(100);

        final Label errorMessage = new Label("Error", skin, Constants.TITLE_FONT);
        //errorMessage.setFontScale(2);
        errorDialog.getContentTable().add(errorMessage);


        TextButton mainMenuButton = new TextButton("BACK", skin);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGameStateManager.pop();
            }
        });

        playTestButton = new TextButton("PLAY", skin);
        playTestButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TextButton button = (TextButton) event.getListenerActor();
                if(button != null) {
                    if (button.isDisabled()) {
                        errorMessage.setText("Level doesn't meet requirements:\nA player and enemy are needed at least");
                        errorDialog.show(mStage);
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



        saveButton = new TextButton("SAVE", skin);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TextButton button = (TextButton) event.getListenerActor();
                if(button != null){
                    if(button.isDisabled()) {
                        errorMessage.setText("Level doesn't meet requirements:\nA player and enemy are needed at least");
                        errorDialog.show(mStage);
                        return;
                    }
                    if(!mLevelManager.addLevel(editorToLevel())){
                        errorMessage.setText("Level already exists");
                        errorDialog.show(mStage);
                        System.out.println("Level already exists");
                    }

                }
            }
        });




        typeButtonGroup = new ButtonGroup();
        typeButtonGroup.setMinCheckCount(1);
        typeButtonGroup.setMaxCheckCount(1);
        typeButtonGroup.setUncheckLast(true);

        typeButtons = new LinkedList<ImageTextButton>();
        for(int i = 0; i < LevelManager.LevelMapTiles.values().length; i ++){
            String s = LevelManager.LevelMapTiles.values()[i].name();
            ImageTextButton button = new ImageTextButton(s, skin, "radio");
            typeButtons.add(button);
            typeButtonGroup.add(button);
        }
        typeButtons.getFirst().setChecked(true);

        float typeButtonPad = 10f;
        Table typeButtonTable = new Table();
        typeButtonTable.setTransform(true);
        typeButtonTable.defaults().expandX().left();
        for(ImageTextButton button : typeButtons){
            typeButtonTable.add(button).pad(typeButtonPad);
            typeButtonTable.row();
        }


        float levelTileSize = 50f;
        float levelTilePad = 1f;
        Table levelMapTable = new Table();
        levelMapTable.setTransform(true);

        levelMapButtons = new LinkedList<ImageButton>();
        levelMap = new int[levelHeight][levelWidth];

        for(int y = 0; y < levelHeight; y++){
            for(int x = 0; x < levelWidth; x++){
                ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
                buttonStyle.up = skin.getDrawable("button-c");
                buttonStyle.down = skin.getDrawable("button-p");
                buttonStyle.over = skin.getDrawable("button-h");
                buttonStyle.disabled = skin.getDrawable("button-d");

                ImageButton imgButton = new ImageButton(skin);
                imgButton.setStyle(buttonStyle);
                imgButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        ImageButton button = (ImageButton) event.getListenerActor();
                        if(button != null){
                            if(button.isDisabled()) return;
                            setButtonType(button, LevelManager.LevelMapTiles.values()[typeButtonGroup.getCheckedIndex()]);
                            updateButtonsDisabled(isValidLevel());
                        }

                    }
                });
                levelMapButtons.add(imgButton);
                levelMapTable.add(imgButton).pad(levelTilePad).width(levelTileSize).height(levelTileSize);

                if(y == 0 || y == levelHeight - 1 || x == 0 || x == levelWidth - 1) {
                    imgButton.setDisabled(true);
                    setButtonType(imgButton, LevelManager.LevelMapTiles.WALL);
                } else {
                    setButtonType(imgButton, LevelManager.LevelMapTiles.FLOOR);
                }
            }
            levelMapTable.row();
        }


        updateButtonsDisabled(isValidLevel());


        float optionButtonsPad = 10f;
        float optionButtonsWidth = 100f;
        float optionButtonsHeight = 50f;
        Table optionButtonsContainer = new Table();
        optionButtonsContainer.setTransform(true);
        optionButtonsContainer.add(mainMenuButton).pad(optionButtonsPad).width(optionButtonsWidth).height(optionButtonsHeight);
        optionButtonsContainer.add(playTestButton).pad(optionButtonsPad).width(optionButtonsWidth).height(optionButtonsHeight);
        optionButtonsContainer.add(saveButton).pad(optionButtonsPad).width(optionButtonsWidth).height(optionButtonsHeight);
        optionButtonsContainer.add(clearButton).pad(optionButtonsPad).width(optionButtonsWidth).height(optionButtonsHeight);


        //Todo use a scroll pane to store the level map


        Table levelEditingTable = new Table();
        levelEditingTable.setTransform(true);
        levelEditingTable.add(typeButtonTable).padRight(50);
        levelEditingTable.add(levelMapTable);

        Table mainRoot = new Table();
        mainRoot.align(Align.center);
        mainRoot.setBounds(0,0, mStage.getWidth(), mStage.getHeight());
        mainRoot.padBottom(20f);
        mainRoot.add(optionButtonsContainer);
        mainRoot.row();
        mainRoot.add(levelEditingTable);

        mStage.addActor(mainRoot);
    }

    private void setButtonType(ImageButton button, LevelManager.LevelMapTiles type){
        int buttonIndex = levelMapButtons.indexOf(button);
        int x = buttonIndex % levelWidth;
        int y = buttonIndex / levelWidth;
        int typeIndex = LevelManager.LevelMapTiles.valueOf(type.name()).ordinal();

        if(type == LevelManager.LevelMapTiles.PLAYER){
            handlePlayerPlacement(buttonIndex);
        } else {
            if(getButtonType(button) == LevelManager.LevelMapTiles.PLAYER){
                hasPlayer = false;
            }
        }


        TextureRegionDrawable image = tileImages[typeIndex];
        button.getStyle().imageUp = image;
        button.getStyle().imageDown = image;

        levelMap[y][x] = typeIndex;
    }

    private boolean isValidLevel(){
        if(!hasPlayer) return false;
        int enemyCode = LevelManager.LevelMapTiles.valueOf(LevelManager.LevelMapTiles.ENEMY.name()).ordinal();
        for(int y = 0; y < levelHeight; y++) {
            for(int x = 0; x < levelWidth; x++){
                if(levelMap[y][x] == enemyCode) return true;
            }
        }
        return false;
    }

    private void updateButtonsDisabled(boolean isViable){
        saveButton.setDisabled(!isViable);
        playTestButton.setDisabled(!isViable);
    }

    private void handlePlayerPlacement(int buttonIndex){
        if(hasPlayer){
            ImageButton button = levelMapButtons.get(currentPlayerButtonIndex);
            int typeIndex = LevelManager.LevelMapTiles.valueOf(LevelManager.LevelMapTiles.FLOOR.name()).ordinal();
            TextureRegionDrawable image = tileImages[typeIndex];
            button.getStyle().imageUp = image;
            button.getStyle().imageDown = image;

            int x = currentPlayerButtonIndex % levelWidth;
            int y = currentPlayerButtonIndex / levelWidth;
            levelMap[y][x] = typeIndex;
            System.out.println("Player reset: " + levelMap[y][x]);
        }

        hasPlayer = true;
        currentPlayerButtonIndex = buttonIndex;
    }

    private int levelMapIntOf(LevelManager.LevelMapTiles type){
        return LevelManager.LevelMapTiles.valueOf(type.name()).ordinal();
    }

    private LevelManager.LevelMapTiles getButtonType(ImageButton button){
        int buttonIndex = levelMapButtons.indexOf(button);
        int x = buttonIndex % levelWidth;
        int y = buttonIndex / levelWidth;
        return LevelManager.LevelMapTiles.values()[levelMap[y][x]];
    }

    private void clearLevel(){
        hasPlayer = false;
        for(int y = 1; y < levelHeight - 1; y++) {
            for(int x = 1; x < levelWidth - 1; x++){
                setButtonType(levelMapButtons.get(y * levelWidth + x), LevelManager.LevelMapTiles.FLOOR);
            }
        }
    }

    private GameLevel editorToLevel(){
        int[][] newLevelMap = new int[levelHeight][levelWidth];
        for(int y = 0; y < levelHeight; y++){
            for(int x = 0; x < levelWidth; x++){
                newLevelMap[y][x] = levelMap[y][x];
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
        if(mStage == null) return;
        Gdx.input.setInputProcessor(mStage);
    }

    @Override
    public void dispose() {
        mLevelManager.setIsPlaytest(false);
        mStage.dispose();
    }
}
