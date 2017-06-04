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

package com.riaanvo.planettanks.LevelFramework;

/**
 * Created by riaanvo on 29/5/17.
 */

public class GameLevel {
    private String mLevelName;
    private int[][] mLevelMap;
    private boolean mUnlocked;
    private boolean mUserGenerated;

    public GameLevel() {
    }


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

    public boolean isUnlocked() {
        return mUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        mUnlocked = unlocked;
    }

    public boolean isUserGenerated() {
        return mUserGenerated;
    }

    public boolean isMatchingLevel(int[][] levelMap) {
        if (levelMap.length != mLevelMap.length || levelMap[0].length != mLevelMap[0].length)
            return false;
        for (int z = 0; z < levelMap.length; z++) {
            for (int x = 0; x < levelMap[0].length; x++) {
                if (levelMap[z][x] != mLevelMap[z][x]) return false;
            }
        }
        return true;
    }
}
