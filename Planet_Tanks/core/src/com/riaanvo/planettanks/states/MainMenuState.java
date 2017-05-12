package com.riaanvo.planettanks.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.riaanvo.planettanks.managers.ContentManager;

/**
 * Created by riaanvo on 9/5/17.
 */

public class MainMenuState extends State{
    public static final String SKIN_KEY = "android/assets/Fonts/uiskin.json";
    public static final String MONOFONT_KEY = "android/assets/Fonts/MonoFont.fnt";

    private boolean isLoading;
    private ContentManager mContentManager;
    private Stage mStage;
    private Skin mSkin;
    private Table mTable;
    private TextButton mStartButton;
    private TextButton mQuitButton;

    public MainMenuState(){
        mContentManager = ContentManager.get();
        //mContentManager.addSkin(SKIN_KEY);
        mContentManager.getAssetManager().load(SKIN_KEY, Skin.class);
        mContentManager.addBitmapFont(MONOFONT_KEY);

        //Gdx.app.log("AssetPath", Gdx.files.internal(SKIN_KEY).file().getAbsolutePath());

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

        mTable.setPosition(0, Gdx.graphics.getHeight());

        mSkin = mContentManager.getSkin(SKIN_KEY);
        //mSkin = new Skin();
        //mSkin.add(MONOFONT_KEY, mContentManager.getBitmapFont(MONOFONT_KEY));

        mStartButton = new TextButton("New Game", mSkin);
        mQuitButton = new TextButton("Quit", mSkin);

        mTable.add(mStartButton);
        mTable.add(mQuitButton);

        mStage.addActor(mTable);
        Gdx.input.setInputProcessor(mStage);
    }
}
