package com.riaanvo.planettanks.managers;


import com.riaanvo.planettanks.IActivityRequestHandler;

/**
 * Created by riaanvo on 3/6/17.
 */

public class AdManager {

    private IActivityRequestHandler mHandler;

    private static AdManager sAdManager;
    public static AdManager get(){
        if(sAdManager == null) sAdManager = new AdManager();
        return sAdManager;
    }

    public void setHandler(IActivityRequestHandler handler){
        mHandler = handler;
    }

    public void showInterstitialAd(){
        if(mHandler != null){
            mHandler.showInterstitialAd();
        }
    }

    public void dispose(){
        mHandler = null;
    }
}
