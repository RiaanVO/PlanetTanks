package com.riaanvo.planettanks.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
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
 * Created by riaanvo on 26/5/17.
 */

public class LevelManager {
    public enum LevelMapTiles {
        FLOOR,
        WALL,
        PLAYER,
        SPIKES,
        ENEMY
    }

    private static LevelManager sLevelManager;

    public static LevelManager get() {
        if (sLevelManager == null) sLevelManager = new LevelManager();
        return sLevelManager;
    }

    private LinkedList<GameLevel> levels = new LinkedList<GameLevel>();
    private int currentLevelIndex;

    private GameStateManager mGameStateManager;
    private GameObjectManager mGameObjectManager;
    private ContentManager mContentManager;
    private CameraController mCameraController;
    private Player player;

    //End of level conditions
    private GameLevel currentLevel;
    private int startingNumEnemies;
    private int currentNumEnemies;
    private float showEndScreenDelay;
    private float delayTimer;
    private boolean playerDead;
    private boolean levelCleared;
    private boolean endDelayOver;
    private int playerScore;

    private boolean levelToLoadSet;

    private LevelManager() {
        mGameStateManager = GameStateManager.get();
        mGameObjectManager = GameObjectManager.get();
        mContentManager = ContentManager.get();
        mCameraController = CameraController.get();

        levelToLoadSet = false;

        showEndScreenDelay = 0.5f;
        delayTimer = 0;

        //String demo1Name = "DemoLevel1";
        int[][] demo1Map = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 3, 3, 3, 3, 1},
                {1, 0, 0, 1, 0, 0, 0, 0, 0, 1},
                {1, 2, 0, 1, 1, 1, 0, 0, 4, 1},
                {1, 0, 0, 1, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 3, 3, 3, 3, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        //String demo2Name = "DemoLevel2";
        int[][] demo2Map = {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 3, 3, 3, 3, 0, 1, 3, 4, 3, 1, 0, 0, 0, 0, 4, 0, 0, 1},
                {1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 3, 3, 3, 1, 0, 3, 0, 0, 0, 0, 0, 1},
                {1, 2, 0, 1, 1, 1, 0, 0, 4, 0, 3, 3, 3, 3, 3, 0, 3, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 3, 3, 3, 1, 0, 3, 0, 0, 0, 0, 0, 1},
                {1, 0, 0, 0, 0, 3, 3, 3, 3, 0, 1, 3, 4, 3, 1, 0, 0, 0, 0, 4, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        for(int i = 1; i < 18; i ++){
            if(i % 2 == 0){
                levels.add(new GameLevel(i + "", demo1Map, true, false));
            } else {
                levels.add(new GameLevel(i + "", demo2Map, false, false));
            }
        }

        currentLevelIndex = 0;
    }

    public void update(float deltaTime) {
        if (!playerDead && !levelCleared) {
            checkGameOver();
            return;
        }

        if (delayTimer == 0) System.out.println("Starting end Delay");
        delayTimer += deltaTime;
        if (!endDelayOver && delayTimer > showEndScreenDelay) {
            endDelayOver = true;
            if (playerDead) {
                playerDeadGameOver();
            } else if (levelCleared) {
                levelClearedGameOver();
            }

        }
    }

    private void checkGameOver() {
        if (player.isDead()) {
            playerDead = true;
        } else if (currentNumEnemies <= 0) {
            levelCleared = true;
        }
    }

    private void playerDeadGameOver() {
        System.out.println("You loose");
        mGameStateManager.push(new GameOverState());
    }

    private void levelClearedGameOver() {
        System.out.println("You win");
        System.out.println("Score: " + playerScore);
        mGameStateManager.push(new LevelCompleteState());
    }

    public void RestartLevel(){
        LoadLevel(currentLevelIndex);
    }

    public void LoadLevel(String levelName) {
        GameLevel level = getLevel(levelName);
        if (level != null) loadLevel(level);
    }

    public void LoadLevel(int levelIndex) {
        if (levelIndex < levels.size()) {
            loadLevel(levels.get(levelIndex));
        }
    }

    private void loadLevel(GameLevel level) {
        unloadLevel();

        mCameraController.CreatePerspective(45, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 1f, 300f, new Vector3(0, 0, 0), new Vector3(0, 20, 10));

        playerDead = false;
        levelCleared = false;
        endDelayOver = false;
        delayTimer = 0;

        startingNumEnemies = 0;
        playerScore = 0;

        int[][] levelMap = level.getLevelMap();
        for (int z = 0; z < levelMap.length; z++) {
            for (int x = 0; x < levelMap[0].length; x++) {
                Vector3 position = new Vector3(Constants.TILE_SIZE / 2 + Constants.TILE_SIZE * x, 0, Constants.TILE_SIZE / 2 + Constants.TILE_SIZE * z);
                addGameObject(position, LevelMapTiles.values()[levelMap[z][x]]);
            }
        }

        currentNumEnemies = startingNumEnemies;
        currentLevelIndex = levels.indexOf(level);
        currentLevel = level;
    }

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

    private void addFloor(Vector3 position) {
        Model floorPlane = mContentManager.getFloorPlane();
        TextureAttribute textureAttribute = new TextureAttribute(TextureAttribute.Diffuse, mContentManager.getTexture(Constants.FLOOR_TEXTURE));
        floorPlane.materials.get(0).set(textureAttribute);
        mGameObjectManager.addGameObject(new FloorTile(floorPlane, position));
    }

    private void addWall(Vector3 position) {
        Vector3 adjustedPosition = position.cpy().add(new Vector3(0, Constants.TILE_SIZE / 2, 0));
        mGameObjectManager.addGameObject(new WallSegment(mContentManager.getWallSegment(), adjustedPosition, new Vector3(Constants.TILE_SIZE, Constants.TILE_SIZE, Constants.TILE_SIZE)));
    }

    private void addPlayer(Vector3 position) {
        ColorAttribute playerColour = new ColorAttribute(ColorAttribute.Diffuse, Color.BLUE);
        TankController playerTank = new TankController(mContentManager.getModel(Constants.BASIC_TANK_BODY_MODEL), mContentManager.getModel(Constants.BASIC_TANK_TURRET_MODEL), playerColour);
        player = new Player(playerTank);
        player.setPosition(new Vector3(5, 0, 5));
        mCameraController.setTrackingObject(player);
        mGameObjectManager.addGameObject(player);
    }

    private void addSpikes(Vector3 position) {
        mGameObjectManager.addGameObject(new SimpleSpikes(mContentManager.getModel(Constants.SIMPLE_SPIKES_BASE), mContentManager.getModel(Constants.SIMPLE_SPIKES_SPIKES), position));
    }

    private void addStationaryEnemy(Vector3 position) {
        ColorAttribute enemyColour = new ColorAttribute(ColorAttribute.Diffuse, Color.RED);
        TankController enemyTank = new TankController(mContentManager.getModel(Constants.BASIC_TANK_BODY_MODEL), mContentManager.getModel(Constants.BASIC_TANK_TURRET_MODEL), enemyColour);
        BasicEnemy enemy = new BasicEnemy(enemyTank);
        enemy.setPosition(position);
        mGameObjectManager.addGameObject(enemy);
        startingNumEnemies++;
    }

    public void unloadLevel() {
        GameObjectManager.get().clearGameObjects();
        CollisionManager.get().clearColliders();
    }

    public Player getPlayer() {
        return player;
    }

    public void EnemyKilled(int pointsToAdd) {
        currentNumEnemies--;
        playerScore += pointsToAdd;
    }

    public boolean isAnotherLevel() {
        return currentLevelIndex < levels.size() - 1;
    }

    public void loadNextLevel() {
        LoadLevel(currentLevelIndex + 1);
    }

    public GameLevel getLevel(String levelName) {
        for (GameLevel level : levels) {
            if (level.isLevel(levelName)) return level;
        }
        return null;
    }

    public GameLevel getLevel(int index){
        if(index < levels.size()){
            return levels.get(index);
        }
        return null;
    }

    public String getLevelName(){
        String s = "";
        if(currentLevel != null){
            s += currentLevel.getLevelName();
        }
        return s;
    }

    public int getNumLevels(){
        return levels.size();
    }

    public void setLevelToLoad(int levelIndex){
        currentLevelIndex = levelIndex;
        levelToLoadSet = true;
    }

    public boolean isLevelToLoadSet(){
        return levelToLoadSet;
    }

    public void loadSetLevel(){
        levelToLoadSet = false;
        LoadLevel(currentLevelIndex);
    }
}
