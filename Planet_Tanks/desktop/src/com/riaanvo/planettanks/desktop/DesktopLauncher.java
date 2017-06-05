package com.riaanvo.planettanks.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.riaanvo.planettanks.Constants;
import com.riaanvo.planettanks.PlanetTanks;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Planet Tanks";

		config.height = (int)Constants.VIRTUAL_SCREEN_HEIGHT;
		config.width = (int)Constants.VIRTUAL_SCREEN_WIDTH;
		//new LwjglApplication(new PlanetTanks(), config);
	}
}
