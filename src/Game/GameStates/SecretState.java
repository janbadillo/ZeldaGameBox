package Game.GameStates;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

import Display.UI.UIManager;
import Main.Handler;
import Resources.Images;

public class SecretState extends State {

	private UIManager uiManager;
	public SecretState(Handler handler) { //handler 
		super(handler);
		refresh();
	}

	@Override
	public void tick() {

	}

	@Override //final message after you died -CS
	public void render(Graphics g) {

		g.drawImage(Images.rA,0,0,handler.getWidth(),handler.getHeight(),null);
		
	}

	@Override
	public void refresh() {
	}
}

