package com.riaanvo.planettanks;

import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class AndroidLauncher extends AndroidApplication implements IActivityRequestHandler {

	//Actual id
	//ca-app-pub-6385922239703193~3458503064

	private InterstitialAd mInterstitialAd;
	private final int SHOW_AD = 0;
	protected Handler handler;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
					case SHOW_AD:
					{
						if(mInterstitialAd.isLoaded()){
							mInterstitialAd.show();
						} else {
							Log.d("RICKY", "Add not loaded");
						}
						break;
					}
				}
			}
		};

		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
		mInterstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				// Load the next interstitial.
				mInterstitialAd.loadAd(new AdRequest.Builder().build());
			}

		});

		AdRequest request = new AdRequest.Builder().build();

		mInterstitialAd.loadAd(request);


		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		initialize(new PlanetTanks(this), config);
	}

	@Override
	public void showInterstitialAd() {
		handler.sendEmptyMessage(SHOW_AD);
	}
}
