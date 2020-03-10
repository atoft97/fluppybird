package com.ingin.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ingin.game.FluppyBird;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = FluppyBird.WIDTH;
		config.height = FluppyBird.HEIGHT;
		config.title = FluppyBird.TITLE;

		new LwjglApplication(new FluppyBird(), config);
	}
}
