package com.riaanvo.planettanks.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.managers.ContentManager;

/**
 * Created by riaanvo on 9/5/17.
 */

public class SplashScreenState extends State {
    private ContentManager mContentManager;
    private Stage mStage;
    private boolean loading;
    private boolean hasTransitioned;

    private Image backgroundImage;

    private float duration = 3;

    public SplashScreenState(){
        mContentManager = ContentManager.get();
        mStage = new Stage();

        //Load required textures and fonts
        mContentManager.addTexture(Constants.SPLASH_BACKGROUND);

        loading = true;
        hasTransitioned = false;
    }

    @Override
    public void update(float deltaTime) {
        duration -= deltaTime;
        if(duration < 0 && !hasTransitioned){
            hasTransitioned = true;
            mGameStateManager.push(new TransitionState(this, new MainMenuState(), TransitionState.TransitionType.BLACK_FADE_REPLACE));
            //mGameStateManager.setState(new MainMenuState());
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch, ModelBatch modelBatch) {
        if(loading){
            if(mContentManager.assetManagerUpdate()){
                loaded();
            }
            return;
        }

        mStage.draw();

    }

    private void loaded(){
        loading = false;

        backgroundImage = new Image(mContentManager.getTexture(Constants.SPLASH_BACKGROUND));
        backgroundImage.setPosition(0,0);
        backgroundImage.setSize(mStage.getWidth(), mStage.getHeight());

        mStage.addActor(backgroundImage);
    }

    @Override
    public void dispose() {
        mStage.dispose();
    }
}
