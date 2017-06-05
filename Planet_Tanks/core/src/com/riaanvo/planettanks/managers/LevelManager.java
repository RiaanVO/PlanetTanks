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

package com.riaanvo.planettanks.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.GameObjects.BasicEnemy;
import com.riaanvo.planettanks.GameObjects.CameraController;
import com.riaanvo.planettanks.GameObjects.FloorTile;
import com.riaanvo.planettanks.GameObjects.Player;
import com.riaanvo.planettanks.GameObjects.SimpleSpikes;
import com.riaanvo.planettanks.GameObjects.TankController;
import com.riaanvo.planettanks.GameObjects.WallSegment;
import com.riaanvo.planettanks.LevelFramework.GameLevel;
import com.riaanvo.planettanks.states.GameOverState;
import com.riaanvo.planettanks.states.LevelCompleteState;

import java.util.LinkedList;

/**
 * This class manages the list of levels in the game and can load a level for playing.
 * It also provides functionality to save the state of the mLevels (unlocked or not) and the
 * user made mLevels.
 */

public class LevelManager {
    //Ids for the different tiles
    public enum LevelMapTiles {
        FLOOR,
        WALL,
        PLAYER,
        SPIKES,
        ENEMY
    }

    private static LevelManager sLevelManager;

    /**
     * Gets the current instance of the level manager. Creates one if there isn't an instance
     *
     * @return the instance of the level manager
     */
    public static LevelManager get() {
        if (sLevelManager == null) sLevelManager = new LevelManager();
        return sLevelManager;
    }

    private LinkedList<GameLevel> mLevels = new LinkedList<GameLevel>();
    private int mCurrentLevelIndex;

    //Managers
    private GameStateManager mGameStateManager;
    private GameObjectManager mGameObjectManager;
    private ContentManager mContentManager;
    private CameraController mCameraController;
    private Player mPlayer;

    //End of level conditions
    private GameLevel mCurrentLevel;
    private int mStartingNumEnemies;
    private int mCurrentNumEnemies;
    private float mShowEndScreenDelay;
    private float mDelayTimer;
    private boolean mPlayerDead;
    private boolean mLevelCleared;
    private boolean mEndDelayOver;
    private int mPlayerScore;

    //Level loading conditions
    private boolean mLoadByIndex;
    private boolean mIsPlaytest;

    private LevelManager() {
        mCurrentLevelIndex = 0;

        mGameStateManager = GameStateManager.get();
        mGameObjectManager = GameObjectManager.get();
        mContentManager = ContentManager.get();
        mCameraController = CameraController.get();

        mShowEndScreenDelay = 0.2f;
        mDelayTimer = 0;

        mLoadByIndex = true;
        mIsPlaytest = false;

        //TODO: remove these hard coded levels
//        //String demo1Name = "DemoLevel1";
//        int[][] demo1Map = {
//                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
//                {1, 0, 0, 0, 0, 3, 3, 3, 3, 1},
//                {1, 0, 0, 1, 0, 0, 0, 0, 0, 1},
//                {1, 2, 0, 1, 1, 1, 0, 0, 4, 1},
//                {1, 0, 0, 1, 0, 0, 0, 0, 0, 1},
//                {1, 0, 0, 0, 0, 3, 3, 3, 3, 1},
//                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
//        };
//
//        //String demo2Name = "DemoLevel2";
//        int[][] demo2Map = {
//                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
//                {1, 0, 0, 0, 0, 3, 3, 3, 3, 0, 1, 3, 4, 3, 1, 0, 0, 0, 0, 4, 0, 0, 1},
//                {1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 3, 3, 3, 1, 0, 3, 0, 0, 0, 0, 0, 1},
//                {1, 2, 0, 1, 1, 1, 0, 0, 4, 0, 3, 3, 3, 3, 3, 0, 3, 1, 1, 1, 1, 1, 1},
//                {1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 3, 3, 3, 1, 0, 3, 0, 0, 0, 0, 0, 1},
//                {1, 0, 0, 0, 0, 3, 3, 3, 3, 0, 1, 3, 4, 3, 1, 0, 0, 0, 0, 4, 0, 0, 1},
//                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
//        };


        LoadCoreLevels();
        LoadPlayerLevels();
    }

    /**
     * Attempts to load the core game levels from the levels file located in the assets.
     * Sets each level state based on the users stored level states
     */
    private void LoadCoreLevels() {
        FileHandle handle = Gdx.files.internal(Constants.CORE_LEVELS_FILE);
        if (handle.exists()) {
            mLevels.addAll(LoadStoredLevels(handle.readString()));
        } else {
            System.out.println("Failed to load files");
        }

        //Get the state of the core levels from the apps preferences
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFERENCES_KEY);
        String levelsString = prefs.getString(Constants.CORE_LEVELS_KEY, "");
        if (!levelsString.equals("")) {
            LinkedList<GameLevel> prefLevels = LoadStoredLevels(levelsString);
            //Check that the user hasn't altered the number of core levels
            if (mLevels.size() != prefLevels.size()) return;
            for (int i = 0; i < mLevels.size(); i++) {
                mLevels.get(i).setUnlocked(prefLevels.get(i).isUnlocked());
            }
        }
    }

    /**
     * Saves the state of the core levels into the apps shared preferences
     * TODO: alter the data stored to just that of its unlocked state
     */
    private void SaveCoreLevels() {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFERENCES_KEY);
        String coreLevels = levelsToJson(getCoreLevels());
        System.out.println(coreLevels);
        prefs.putString(Constants.CORE_LEVELS_KEY, coreLevels);
        prefs.flush();
    }

    /**
     * Attempts to load the player created levels from the apps shared preferences
     */
    private void LoadPlayerLevels() {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFERENCES_KEY);
        String levelsString = prefs.getString(Constants.PLAYER_LEVELS_KEY, "");
        if (!levelsString.equals("")) {
            mLevels.addAll(LoadStoredLevels(levelsString));
        }
    }


    /**
     * Saves the players created levels to the apps shared preferences
     */
    private void SavePlayerLevels() {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFERENCES_KEY);
        String userLevels = levelsToJson(getUserMadeLevels());
        prefs.putString(Constants.PLAYER_LEVELS_KEY, userLevels);
        prefs.flush();
    }

    /**
     * Updates the level conditions and checks to see if the game should end
     *
     * @param deltaTime the time since the last update
     */
    public void update(float deltaTime) {
        //Check if the game is over only if it hasn't been ended yet
        if (!mPlayerDead && !mLevelCleared) {
            checkGameOver();
            return;
        }

        //Increment the levels timer used to transition to the next screen
        mDelayTimer += deltaTime;
        if (!mEndDelayOver && mDelayTimer > mShowEndScreenDelay) {
            mEndDelayOver = true;
            if (mPlayerDead) {
                playerDeadGameOver();
            } else if (mLevelCleared) {
                levelClearedGameOver();
            }

        }
    }

    /**
     * Check if either the player is dead or the enemies are dead and set the level condition
     */
    private void checkGameOver() {
        if (mPlayer.isDead()) {
            mPlayerDead = true;
        } else if (mCurrentNumEnemies <= 0) {
            mLevelCleared = true;
        }
    }

    /**
     * Display the game over screen
     */
    private void playerDeadGameOver() {
        mGameStateManager.push(new GameOverState());
    }

    /**
     * Display the level cleared screen
     * TODO: use the score functionality to calculate accuracy and display it to the player.
     */
    private void levelClearedGameOver() {
        System.out.println("Score: " + mPlayerScore);
        mGameStateManager.push(new LevelCompleteState());
    }

    /**
     * Loads the current level into the game again
     */
    public void RestartLevel() {
        LoadLevel();
    }

    /**
     * Clears the managers of the game objects and colliders associated with the current level
     */
    public void unloadLevel() {
        GameObjectManager.get().clearGameObjects();
        CollisionManager.get().clearColliders();
    }

    /**
     * Checks if there is another level to play
     *
     * @return if there is another level to play
     */
    public boolean isAnotherLevel() {
        return mCurrentLevelIndex < mLevels.size() - 1 && !mIsPlaytest;
    }

    /**
     * Unlocks the next level
     */
    public void UnlockNextLevel() {
        if (isAnotherLevel()) {
            mLevels.get(mCurrentLevelIndex + 1).setUnlocked(true);
        }
    }

    /**
     * Loads the next level into the game
     */
    public void LoadNextLevel() {
        mLoadByIndex = true;
        mCurrentLevelIndex += 1;
        LoadLevel();
    }

    /**
     * Checks to see how the level will be loaded and loads the level
     */
    public void LoadLevel() {
        //Checks if it should load the current level or load the level it gets from the index
        if (mLoadByIndex) {
            loadLevel(mLevels.get(mCurrentLevelIndex));
        } else {
            loadLevel(mCurrentLevel);
        }
    }

    /**
     * Loads the given level into the scene by creating all the game objects.
     *
     * @param level
     */
    private void loadLevel(GameLevel level) {
        //Unload the last level and create a new camera
        unloadLevel();
        mCameraController.CreatePerspective(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1f, 300f, new Vector3(0, 0, 0), new Vector3(0, 20, 10));

        //if (!level.isUnlocked()) level.setUnlocked(true);

        //Reset all level conditions and values
        mPlayerDead = false;
        mLevelCleared = false;
        mEndDelayOver = false;
        mDelayTimer = 0;

        mStartingNumEnemies = 0;
        mPlayerScore = 0;

        //loop through the level map and create the game objects
        int[][] levelMap = level.getLevelMap();
        for (int z = 0; z < levelMap.length; z++) {
            for (int x = 0; x < levelMap[0].length; x++) {
                Vector3 position = new Vector3(Constants.TILE_SIZE / 2 + Constants.TILE_SIZE * x, 0, Constants.TILE_SIZE / 2 + Constants.TILE_SIZE * z);
                addGameObject(position, LevelMapTiles.values()[levelMap[z][x]]);
            }
        }

        //set the final level conditions and store the current levels values
        mCurrentNumEnemies = mStartingNumEnemies;
        if (mLoadByIndex) mCurrentLevelIndex = mLevels.indexOf(level);
        mCurrentLevel = level;
    }

    /**
     * Sets the index of the level to load
     *
     * @param levelIndex of the level to be loaded
     */
    public void setLevelToLoad(int levelIndex) {
        mCurrentLevelIndex = levelIndex;
        mLoadByIndex = true;
    }

    /**
     * Sets the current level varible to the passed in level and changes the load by level index flag
     * to false
     *
     * @param level that will be loaded
     */
    public void setLevelToLoad(GameLevel level) {
        mCurrentLevel = level;
        mLoadByIndex = false;
    }

    /**
     * Adds the provided points to the players score. Called when an enemy is killed
     *
     * @param pointsToAdd to the players score
     */
    public void EnemyKilled(int pointsToAdd) {
        mCurrentNumEnemies--;
        mPlayerScore += pointsToAdd;
    }

    public void setIsPlaytest(boolean playtest) {
        mIsPlaytest = playtest;
    }

    /**
     * Adds a level to the list of levels if it does not already have the same level map as a
     * level already in the list of levels.
     *
     * @param newLevel the level to be added
     * @return if the level was added
     */
    public boolean addLevel(GameLevel newLevel) {
        for (GameLevel level : mLevels) {
            if (level.isMatchingLevel(newLevel.getLevelMap())) {
                return false;
            }
        }
        mLevels.add(newLevel);
        return true;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    /**
     * Gets a level based on the name passed in
     *
     * @param levelName the name of the level
     * @return the level that matches that name
     */
    public GameLevel getLevel(String levelName) {
        for (GameLevel level : mLevels) {
            if (level.isLevel(levelName)) return level;
        }
        return null;
    }

    /**
     * Gets the level at this index
     *
     * @param index of the level
     * @return the level at that index in the list
     */
    public GameLevel getLevel(int index) {
        if (index < mLevels.size()) {
            return mLevels.get(index);
        }
        return null;
    }

    /**
     * Gets the current levels name
     *
     * @return the name of the current level
     */
    public String getLevelName() {
        String s = "";
        if (mCurrentLevel != null) {
            s += mCurrentLevel.getLevelName();
        }
        return s;
    }

    public int getNumLevels() {
        return mLevels.size();
    }

    /**
     * Calls the add method on the type of game object provided.
     *
     * @param position of the game object in the world
     * @param type of the gameobject to be added
     */
    private void addGameObject(Vector3 position, LevelMapTiles type) {
        switch (type) {
            case FLOOR:
                addFloor(position);
                break;
            case WALL:
                addWall(position);
                break;
            case PLAYER:
                addFloor(position);
                addPlayer(position);
                break;
            case SPIKES:
                addSpikes(position);
                break;
            case ENEMY:
                addFloor(position);
                addStationaryEnemy(position);
                break;
        }
    }

    /**
     * Adds a floor game object at the position provided
     * @param position of the new floor object
     */
    private void addFloor(Vector3 position) {
        Model floorPlane = mContentManager.getFloorPlane();
        TextureAttribute textureAttribute = new TextureAttribute(TextureAttribute.Diffuse, mContentManager.getTexture(Constants.FLOOR_TEXTURE));
        floorPlane.materials.get(0).set(textureAttribute);
        mGameObjectManager.addGameObject(new FloorTile(floorPlane, position));
    }

    /**
     * Adds a wall game object at the position provided
     * @param position of the new wall object
     */
    private void addWall(Vector3 position) {
        Vector3 adjustedPosition = position.cpy().add(new Vector3(0, Constants.TILE_SIZE / 2, 0));
        mGameObjectManager.addGameObject(new WallSegment(mContentManager.getWallSegment(), adjustedPosition, new Vector3(Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE)));
    }

    /**
     * Adds a player game object at the position provided. Also sets up the player variable in the
     * this level manager and the camera controller
     * @param position of the new player object
     */
    private void addPlayer(Vector3 position) {
        ColorAttribute playerColour = new ColorAttribute(ColorAttribute.Diffuse, Color.BLUE);
        TankController playerTank = new TankController(mContentManager.getModel(Constants.BASIC_TANK_BODY_MODEL), mContentManager.getModel(Constants.BASIC_TANK_TURRET_MODEL), playerColour);
        mPlayer = new Player(playerTank);
        mPlayer.setPosition(position);
        mCameraController.setTrackingObject(mPlayer);
        mGameObjectManager.addGameObject(mPlayer);
    }

    /**
     * Adds a spike game object at the position provided.
     * @param position of the new spike object
     */
    private void addSpikes(Vector3 position) {
        mGameObjectManager.addGameObject(new SimpleSpikes(mContentManager.getModel(Constants.SIMPLE_SPIKES_BASE), mContentManager.getModel(Constants.SIMPLE_SPIKES_SPIKES), position));
    }

    /**
     * Adds a stationary enemy game object at the position provided and increments the number of enemies
     * @param position the position of the new enemy
     */
    private void addStationaryEnemy(Vector3 position) {
        ColorAttribute enemyColour = new ColorAttribute(ColorAttribute.Diffuse, Color.RED);
        TankController enemyTank = new TankController(mContentManager.getModel(Constants.BASIC_TANK_BODY_MODEL), mContentManager.getModel(Constants.BASIC_TANK_TURRET_MODEL), enemyColour);
        BasicEnemy enemy = new BasicEnemy(enemyTank);
        enemy.setPosition(position);
        mGameObjectManager.addGameObject(enemy);
        mStartingNumEnemies++;
    }

    public void removeLevel(int levelIndex) {
        mLevels.remove(levelIndex);
    }

    public void removeLevel(GameLevel level) {
        mLevels.remove(level);
    }

    /**
     * Gets all the levels that are not created by the user and returns them in a list
     * @return the list of levels not created by the player
     */
    public LinkedList<GameLevel> getCoreLevels() {
        LinkedList<GameLevel> coreLevels = new LinkedList<GameLevel>();
        for (GameLevel level : mLevels) {
            if (!level.isUserGenerated()) coreLevels.add(level);
        }
        return coreLevels;
    }

    /**
     * Gets all the levels that are created by the player and returns them in a list
     * @return the list of levels created by the player
     */
    public LinkedList<GameLevel> getUserMadeLevels() {
        LinkedList<GameLevel> userMadeLevels = new LinkedList<GameLevel>();

        for (GameLevel level : mLevels) {
            if (level.isUserGenerated()) userMadeLevels.add(level);
        }

        return userMadeLevels;
    }

    /**
     * Converts a list of game levels into a JSON string of objects
     * @param gameLevels A list of game levels
     * @return the string of objects
     */
    public String levelsToJson(LinkedList<GameLevel> gameLevels) {
        Json json = new Json();
        return json.toJson(gameLevels);
    }

    /**
     * Attempts to get a list of levels from the string passed in
     * @param levelsJson the string of JSON level objects
     * @return a list of levels
     */
    public LinkedList<GameLevel> LoadStoredLevels(String levelsJson) {
        LinkedList<GameLevel> levels = new LinkedList<GameLevel>();
        Json json = new Json();

        try{
            levels.addAll(json.fromJson(LinkedList.class, levelsJson));
        } catch (Exception e){
            System.out.println(e.toString());
        }

        return levels;
    }


    /**
     * Saves the levels and clears the levels list
     */
    public void dispose() {
        SavePlayerLevels();
        SaveCoreLevels();
    }
}
