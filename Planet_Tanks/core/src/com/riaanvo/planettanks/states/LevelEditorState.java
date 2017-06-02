package com.riaanvo.planettanks.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
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


    public LevelEditorState(){
        mLevelManager = LevelManager.get();
        mContentManager.loadSkin(Constants.SKIN_KEY);

        mContentManager.loadTexture(Constants.FLOOR_TILE);
        mContentManager.loadTexture(Constants.WALL_TILE);
        mContentManager.loadTexture(Constants.PLAYER_TILE);
        mContentManager.loadTexture(Constants.SPIKES_TILE);
        mContentManager.loadTexture(Constants.ENEMY_TILE);
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

        levelWidth = 12;
        levelHeight = 9;



        TextButton mainMenuButton = new TextButton("BACK", skin);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGameStateManager.pop();
            }
        });

        TextButton playTestButton = new TextButton("PLAY", skin);
        playTestButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mGameStateManager.push(new PlayState());
            }
        });

        TextButton clearButton = new TextButton("CLEAR", skin);
        clearButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Clear hit");
            }
        });

        TextButton saveButton = new TextButton("SAVE", skin);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Save hit");
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
        //levelMapButtons = new ImageButton[levelWidth][levelHeight];

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
                            System.out.println("Button pressed");
                        }

                    }
                });

                if(y == 0 || y == levelHeight - 1 || x == 0 || x == levelWidth - 1) {
                    imgButton.setDisabled(true);
                    setButtonType(imgButton, LevelManager.LevelMapTiles.WALL);
                } else {
                    setButtonType(imgButton, LevelManager.LevelMapTiles.FLOOR);
                }
                levelMapButtons.add(imgButton);
                levelMapTable.add(imgButton).pad(levelTilePad).width(levelTileSize).height(levelTileSize);
            }
            levelMapTable.row();
        }

        levelMap = new int[levelWidth][levelHeight];

        float optionButtonsPad = 10f;
        float optionButtonsWidth = 100f;
        float optionButtonsHeight = 50f;
        Table optionButtonsContainer = new Table();
        optionButtonsContainer.setTransform(true);
        optionButtonsContainer.add(mainMenuButton).pad(optionButtonsPad).width(optionButtonsWidth).height(optionButtonsHeight);
        //optionButtonsContainer.add(playTestButton).pad(optionButtonsPad).width(optionButtonsWidth).height(optionButtonsHeight);
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
        //mainRoot.row();
        //mainRoot.add(mainMenuButton).pad(optionButtonsPad).width(optionButtonsWidth).height(optionButtonsHeight);

//        mainRoot.debug();
//        optionButtonsContainer.debug();
//        levelEditingTable.debug();
//        typeButtonTable.debug();
//        levelMapTable.debug();

        mStage.addActor(mainRoot);
    }

    private void setButtonType(ImageButton button, LevelManager.LevelMapTiles type){
        TextureRegionDrawable image = tileImages[LevelManager.LevelMapTiles.valueOf(type.name()).ordinal()];
        button.getStyle().imageUp = image;
        button.getStyle().imageDown = image;
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
