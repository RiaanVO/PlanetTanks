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


import com.riaanvo.planettanks.IActivityRequestHandler;

/**
 * This class provides the ability to display an add from anywhere in the game. It uses the
 * connection between the game engine and the UI thread to display the ads
 * TODO: implement ad with reward functionality
 */

public class AdManager {

    private IActivityRequestHandler mHandler;

    private static AdManager sAdManager;

    /**
     * Gets the current ad manager and creates one if there isn't already one
     *
     * @return the instance of the ad manager
     */
    public static AdManager get() {
        if (sAdManager == null) sAdManager = new AdManager();
        return sAdManager;
    }

    /**
     * Tells the handler to show an interstitial ad
     * TODO: implement a way to activate many different interstitial ads
     */
    public void showInterstitialAd() {
        if (mHandler != null) {
            mHandler.showInterstitialAd();
        }
    }

    public void setHandler(IActivityRequestHandler handler) {
        mHandler = handler;
    }

    /**
     * Remove the reference to the handler
     */
    public void dispose() {
        mHandler = null;
    }
}
