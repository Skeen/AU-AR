package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
//import com.mygdx.game.MyGdxGame;
//import com.mygdx.game.Cube;
//import com.mygdx.game.Ex2;
import com.mygdx.game.Ex2Cheat;
import com.mygdx.game.Ex1;
import com.mygdx.game.Ex2;
import com.mygdx.game.Ex3;
import com.mygdx.game.Project;
//import com.mygdx.game.HelloCV;

public class DesktopLauncher {
	public static void main (String[] arg) {
		System.loadLibrary("opencv_java2410");
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new Ex3(), config);
	}
}
