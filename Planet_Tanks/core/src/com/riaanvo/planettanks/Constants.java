package com.riaanvo.planettanks;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by riaanvo on 9/5/17.
 */

public class Constants {
    //Game Constants
    public static final float TILE_SIZE = 2f;
    public final static float VIRTUAL_SCREEN_HEIGHT = 540;
    public final static float VIRTUAL_SCREEN_WIDTH = 960;
    private static final float BOUNDS_SCALE = 1.5f;
    public final static Vector3 RENDER_BOUNDS = new Vector3(TILE_SIZE * BOUNDS_SCALE, TILE_SIZE * BOUNDS_SCALE, TILE_SIZE * BOUNDS_SCALE);

    //Resource constants
    public static final String SKIN_KEY = "android/assets/UI/flat-earth-ui.json";
    public static final String TITLE_FONT = "title";
    public static final String DEFAULT_FONT = "font";

    public static final String ENEMY_TILE = "android/assets/UI/LevelEditorTiles/Enemy.png";
    public static final String WALL_TILE = "android/assets/UI/LevelEditorTiles/Wall.png";
    public static final String SPIKES_TILE = "android/assets/UI/LevelEditorTiles/Spikes.png";
    public static final String FLOOR_TILE = "android/assets/UI/LevelEditorTiles/Floor.png";
    public static final String PLAYER_TILE = "android/assets/UI/LevelEditorTiles/Player.png";


    public static final String SPLASH_BACKGROUND = "android/assets/Textures/SplashScreenBackground.png";
    public static final String MAIN_MENU_BACKGROUND = "android/assets/Textures/MainMenuBackground.png";
    public static final String BLACK_TEXTURE = "android/assets/Textures/black.png";
    public static final String FLOOR_TEXTURE = "android/assets/Textures/floor.jpg";
    public static final String BASIC_TANK_BODY_MODEL = "android/assets/Models/Tank/BasicTankBody.obj";
    public static final String BASIC_TANK_TURRET_MODEL = "android/assets/Models/Tank/BasicTankTurret.obj";
    public static final String SIMPLE_SPIKES_SPIKES = "android/assets/Models/Spikes/SimpleSpikesSpikes.obj";
    public static final String SIMPLE_SPIKES_BASE = "android/assets/Models/Spikes/SimpleSpikesBase.obj";

//    //Android constants
//    public static final String SKIN_KEY = "UI/flat-earth-ui.json";
//    public static final String TITLE_FONT = "title";
//    public static final String DEFAULT_FONT = "font";
//
//    public static final String ENEMY_TILE = "UI/LevelEditorTiles/Enemy.png";
//    public static final String WALL_TILE = "UI/LevelEditorTiles/Wall.png";
//    public static final String SPIKES_TILE = "UI/LevelEditorTiles/Spikes.png";
//    public static final String FLOOR_TILE = "UI/LevelEditorTiles/Floor.png";
//    public static final String PLAYER_TILE = "UI/LevelEditorTiles/Player.png";
//
//    public static final String SPLASH_BACKGROUND = "Textures/SplashScreenBackground.png";
//    public static final String MAIN_MENU_BACKGROUND = "Textures/MainMenuBackground.png";
//    public static final String BLACK_TEXTURE = "Textures/black.png";
//    public static final String FLOOR_TEXTURE = "Textures/floor.jpg";
//    public static final String BASIC_TANK_BODY_MODEL = "Models/Tank/BasicTankBody.obj";
//    public static final String BASIC_TANK_TURRET_MODEL = "Models/Tank/BasicTankTurret.obj";
//    public static final String SIMPLE_SPIKES_SPIKES = "Models/Spikes/SimpleSpikesSpikes.obj";
//    public static final String SIMPLE_SPIKES_BASE = "Models/Spikes/SimpleSpikesBase.obj";


}
