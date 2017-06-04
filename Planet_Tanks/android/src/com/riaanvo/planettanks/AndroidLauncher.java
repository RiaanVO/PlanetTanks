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

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * This is the main activity that houses the game. It initialises the game and sets it as the view.
 * Additional this activity contains methods to show interstitial ads to the user. This is done
 * through the use of a mHandler and interface class to allow communication from the game engine to
 * this activity.
 */

public class AndroidLauncher extends AndroidApplication implements IActivityRequestHandler {
    public static final String DEBUG_KEY = "PLANET_TANKS_DEBUG";
    public static final String TEST_AD_KEY = "ca-app-pub-3940256099942544/1033173712";
    private final int SHOW_AD = 0;
    private InterstitialAd mInterstitialAd;
    protected Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Sets up the handler that will manage communication between the game thread an the UI thread
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SHOW_AD: {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            Log.d(DEBUG_KEY, "Ad not loaded");
                        }
                        break;
                    }
                }
            }
        };

        //Create the interstitial ad container and set the key
        mInterstitialAd = new InterstitialAd(this);
        //This key is Googles' developer test key
        //TODO: change the key to the production key in AdMob
        mInterstitialAd.setAdUnitId(TEST_AD_KEY);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        //Set up a request for an ad and send it
        AdRequest request = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(request);


        //Set up app configuration and initialise the game engine
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;
        initialize(new PlanetTanks(this), config);
    }

    /**
     * Called from the game engine as a means to show an add to the user
     */
    @Override
    public void showInterstitialAd() {
        mHandler.sendEmptyMessage(SHOW_AD);
    }
}
