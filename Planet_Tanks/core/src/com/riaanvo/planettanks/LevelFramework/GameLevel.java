package com.riaanvo.planettanks.LevelFramework;

/**
 * Created by riaanvo on 29/5/17.
 */

public class GameLevel {
    private String mLevelName;
    private int[][] mLevelMap;

    public GameLevel(String levelName, int[][] levelMap){
        mLevelName = levelName;
        mLevelMap = levelMap;
    }

    public String getLevelName(){
        return mLevelName;
    }

    public void setLevelName(String newName){
        mLevelName = newName;
    }

    public void setLevelMap(int[][] newLevelMap){
        mLevelMap = newLevelMap;
    }

    public int[][] getLevelMap(){
        return mLevelMap;
    }

    public boolean isLevel(String levelName){
        return mLevelName.equals(levelName);
    }
}
