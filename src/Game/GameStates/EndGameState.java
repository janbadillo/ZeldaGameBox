package Game.GameStates;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import Display.UI.ClickListlener;
import Display.UI.UIImageButton;
import Display.UI.UIManager;
import Main.Handler;
import Resources.Images;

public class EndGameState extends State {

    private UIManager uiManager;
    private int num = 1;
    public EndGameState(Handler handler) { //handler 
        super(handler);
        refresh();
    }

    @Override
    public void tick() {
        handler.getMouseManager().setUimanager(uiManager);
        uiManager.tick();

    }

    @Override //final message after you died -CS
    public void render(Graphics g) {
     
        g.drawImage(Images.gameOverScreen,0,0,handler.getWidth(),handler.getHeight(),null);
        g.setColor(Color.WHITE);
    	g.setFont(new Font("TimesRoman", Font.PLAIN, 70));
		g.drawString("CONTINUE",handler.getWidth()/2-handler.getWidth()/16,handler.getHeight()/2);
		g.drawString("QUIT",handler.getWidth()/2-handler.getWidth()/16,handler.getHeight()/2+handler.getHeight()/12);
		g.drawString("DONT'T PRESS R",handler.getWidth()/2-handler.getWidth()/16,700);
		
		
		if (num == 1){
			g.drawImage(Images.linkHeart[2],handler.getWidth()/2-handler.getWidth()/12-10,handler.getHeight()/2-handler.getHeight()/32-16,40,48,null);
			g.setColor(Color.RED);
			g.drawString("CONTINUE",handler.getWidth()/2-handler.getWidth()/16,handler.getHeight()/2);
		}else if(num==2) {
			g.drawImage(Images.linkHeart[2],handler.getWidth()/2-handler.getWidth()/12-10,handler.getHeight()/2+handler.getHeight()/18-17,40,48,null);
			g.setColor(Color.RED);
			g.drawString("QUIT",handler.getWidth()/2-handler.getWidth()/16,handler.getHeight()/2+handler.getHeight()/12);
		}else{
			g.drawImage(Images.linkHeart[2],handler.getWidth()/2-handler.getWidth()/12-10,651,40,48,null);
			g.setColor(Color.RED);
			g.drawString("DONT'T PRESS R",handler.getWidth()/2-handler.getWidth()/16,700);
		}
		
		if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_UP) ||handler.getKeyManager().keyJustPressed(KeyEvent.VK_W)){
			num=1;
		}else if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_DOWN) || handler.getKeyManager().keyJustPressed(KeyEvent.VK_S)){
			num=2;
		}else if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_R)){
			num=3;	
		}
		
		if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_ENTER) && num == 1){
			State.setState(handler.getZeldaGameState());;
			handler.getMusicHandler().changeMusic("zeldaoverworld.wav");
			
		}else if((handler.getKeyManager().keyJustPressed(KeyEvent.VK_ENTER) && num == 2)){
			State.setState(handler.getMenuState());
			handler.getMusicHandler().changeMusic("nature.wav");
			
		}else if(num == 3) {
			State.setState(handler.getSecretState());
			handler.getMusicHandler().changeMusic("NGGUP.wav");
		}
        
        uiManager.Render(g);

    }


    @Override //New button that goes to MENU STATE-CS
    public void refresh() {
        uiManager = new UIManager(handler);
        handler.getMouseManager().setUimanager(uiManager);


    }
}

