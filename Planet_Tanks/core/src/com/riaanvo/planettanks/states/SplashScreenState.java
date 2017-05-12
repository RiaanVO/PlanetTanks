package com.riaanvo.planettanks.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.riaanvo.planettanks.managers.ContentManager;

/**
 * Created by riaanvo on 9/5/17.
 */

public class SplashScreenState extends State {
    private ContentManager mContentManager;
    private Stage mStage;
    private boolean loading;

    private float duration = 10;

    public SplashScreenState(){
        mContentManager = ContentManager.get();
        mStage = new Stage();

        //Load required textures and fonts


        loading = true;
    }

    @Override
    public void update(float deltaTime) {
        duration -= deltaTime;
        if(duration < 0){
            mGameStateManager.pop();
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
    }

    @Override
    public void dispose() {
        mStage.dispose();
    }
}
