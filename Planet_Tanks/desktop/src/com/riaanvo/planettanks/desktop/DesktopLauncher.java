package com.riaanvo.planettanks.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.riaanvo.planettanks.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Planet Tanks";

		int width = 900;
		int height = width / 16 * 9;
		config.height = height;
		config.width = width;
		new LwjglApplication(new Main(), config);
	}
}
