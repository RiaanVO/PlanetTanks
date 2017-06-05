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

package com.riaanvo.planettanks;

import com.badlogic.gdx.math.Vector3;

/**
 * The constants used throughout the game
 */

public class Constants {
    //Game Constants
    public static final float TILE_SIZE = 2f;
    public static final float VIRTUAL_SCREEN_HEIGHT = 540;
    public static final float VIRTUAL_SCREEN_WIDTH = 960;
    private static final float BOUNDS_SCALE = 1.5f;
    public static final Vector3 RENDER_BOUNDS = new Vector3(TILE_SIZE * BOUNDS_SCALE, TILE_SIZE * BOUNDS_SCALE, TILE_SIZE * BOUNDS_SCALE);

    public static final float TANK_COLLIDER_RADIUS = 0.8f;
    public static final Vector3 TANK_COLLIDER_OFFSET = new Vector3(0, 0.5f, 0);

    //Level files keys
    public static final String PREFERENCES_KEY = "PlanetTanksPrefs";
    public static final String PLAYER_LEVELS_KEY = "PlayerLevels";
    public static final String CORE_LEVELS_FILE = "Levels/Levels.txt";
    public static final String CORE_LEVELS_KEY = "CoreLevels";

    //Game Object Tags
    public static final String TAG_DEFAULT = "GameObject";
    public static final String TAG_PLAYER = "Player";
    public static final String TAG_BASIC_STATIC_ENEMY = "BasicStaticEnemy";
    public static final String TAG_BASIC_SHELL = "Basic shell";

    //Android constants
    public static final String SKIN_KEY = "UI/flat-earth-ui.json";
    public static final String TITLE_FONT = "title";
    public static final String DEFAULT_FONT = "font";

    public static final String ENEMY_TILE = "UI/LevelEditorTiles/Enemy.png";
    public static final String WALL_TILE = "UI/LevelEditorTiles/Wall.png";
    public static final String SPIKES_TILE = "UI/LevelEditorTiles/Spikes.png";
    public static final String FLOOR_TILE = "UI/LevelEditorTiles/Floor.png";
    public static final String PLAYER_TILE = "UI/LevelEditorTiles/Player.png";

    public static final String SPLASH_BACKGROUND = "Textures/SplashScreenBackground.png";
    public static final String MAIN_MENU_BACKGROUND = "Textures/MainMenuBackground.png";
    public static final String BLACK_TEXTURE = "Textures/black.png";
    public static final String FLOOR_TEXTURE = "Textures/floor.jpg";
    public static final String BASIC_TANK_BODY_MODEL = "Models/Tank/BasicTankBody.obj";
    public static final String BASIC_TANK_TURRET_MODEL = "Models/Tank/BasicTankTurret.obj";
    public static final String SIMPLE_SPIKES_SPIKES = "Models/Spikes/SimpleSpikesSpikes.obj";
    public static final String SIMPLE_SPIKES_BASE = "Models/Spikes/SimpleSpikesBase.obj";

    //TODO: remove constants used to pc version
//    //Resource constants
//    public static final String SKIN_KEY = "android/assets/UI/flat-earth-ui.json";
//    public static final String TITLE_FONT = "title";
//    public static final String DEFAULT_FONT = "font";
//
//    public static final String ENEMY_TILE = "android/assets/UI/LevelEditorTiles/Enemy.png";
//    public static final String WALL_TILE = "android/assets/UI/LevelEditorTiles/Wall.png";
//    public static final String SPIKES_TILE = "android/assets/UI/LevelEditorTiles/Spikes.png";
//    public static final String FLOOR_TILE = "android/assets/UI/LevelEditorTiles/Floor.png";
//    public static final String PLAYER_TILE = "android/assets/UI/LevelEditorTiles/Player.png";
//
//
//    public static final String SPLASH_BACKGROUND = "android/assets/Textures/SplashScreenBackground.png";
//    public static final String MAIN_MENU_BACKGROUND = "android/assets/Textures/MainMenuBackground.png";
//    public static final String BLACK_TEXTURE = "android/assets/Textures/black.png";
//    public static final String FLOOR_TEXTURE = "android/assets/Textures/floor.jpg";
//    public static final String BASIC_TANK_BODY_MODEL = "android/assets/Models/Tank/BasicTankBody.obj";
//    public static final String BASIC_TANK_TURRET_MODEL = "android/assets/Models/Tank/BasicTankTurret.obj";
//    public static final String SIMPLE_SPIKES_SPIKES = "android/assets/Models/Spikes/SimpleSpikesSpikes.obj";
//    public static final String SIMPLE_SPIKES_BASE = "android/assets/Models/Spikes/SimpleSpikesBase.obj";

}
