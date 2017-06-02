package com.riaanvo.planettanks.LevelFramework;

/**
 * Created by riaanvo on 29/5/17.
 */

public class GameLevel {
    private String mLevelName;
    private int[][] mLevelMap;
    private boolean mUnlocked;
    private boolean mUserGenerated;

    public GameLevel(){};

    public GameLevel(String levelName, int[][] levelMap, boolean unlocked, boolean userGenerated) {
        mLevelName = levelName;
        mLevelMap = levelMap;
        mUnlocked = unlocked;
        mUserGenerated = userGenerated;
    }

    public String getLevelName() {
        return mLevelName;
    }

    public void setLevelName(String newName) {
        mLevelName = newName;
    }

    public void setLevelMap(int[][] newLevelMap) {
        mLevelMap = newLevelMap;
    }

    public int[][] getLevelMap() {
        return mLevelMap;
    }

    public boolean isLevel(String levelName) {
        return mLevelName.equals(levelName);
    }

    public boolean isUnlocked(){
        return mUnlocked;
    }

    public void setUnlocked(boolean unlocked){
        mUnlocked = unlocked;
    }

    public boolean isUserGenerated(){
        return mUserGenerated;
    }

    public boolean isMatchingLevel(int[][] levelMap){
        if(levelMap.length != mLevelMap.length || levelMap[0].length != mLevelMap[0].length) return false;
        for (int z = 0; z < levelMap.length; z++) {
            for (int x = 0; x < levelMap[0].length; x++) {
                if(levelMap[z][x] != mLevelMap[z][x]) return false;
            }
        }
        return true;
    }
}
