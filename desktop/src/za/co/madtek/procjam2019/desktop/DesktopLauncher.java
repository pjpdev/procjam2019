package za.co.madtek.procjam2019.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import za.co.madtek.procjam2019.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "#PROCJAM 2019";
		config.width = 1280;
		config.height = 720;
		//config.resizable = false;
		new LwjglApplication(new MyGdxGame(), config);
	}
}
