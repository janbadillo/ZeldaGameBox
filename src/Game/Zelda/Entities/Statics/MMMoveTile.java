package Game.Zelda.Entities.Statics;

import Game.GameStates.Zelda.ZeldaMMGameState;
import Game.Zelda.Entities.MMBaseEntity;
import Main.Handler;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import com.sun.javafx.scene.traversal.Direction;

/**
 * Created by AlexVR on 3/14/2020
 */
public class MMMoveTile extends MMBaseEntity {

    public int linkedX,linkedY;
    private Direction dir;

    public MMMoveTile(int x, int y, BufferedImage sprite, Handler handler, Direction dir) {
        super(x, y, sprite,handler);
        bounds = new Rectangle(x ,y ,width,height);
        this.dir = dir;
    }

    @Override
    public void tick() {
        if (handler.getState() instanceof ZeldaMMGameState && ((ZeldaMMGameState)handler.getState()).map.link.interactBounds.intersects(bounds)){
            moveLink();
        }
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(sprite,x ,y,width,height,null);
    }
    
    public void moveLink() {
    	int speed = 3;
    	moveLinkHelper(dir, speed);
    }
    
    public void moveLinkHelper(Direction dir, int speed) {
    	if (speed <= 0) {
    		return;
    	}
    	if(dir == Direction.UP) {
    		((ZeldaMMGameState)handler.getState()).map.link.y -= 1;
    	}else if(dir == Direction.DOWN) {
    		((ZeldaMMGameState)handler.getState()).map.link.y += 1;
    	}else if(dir == Direction.LEFT) {
    		((ZeldaMMGameState)handler.getState()).map.link.x -= 1;
    	}else {
    		((ZeldaMMGameState)handler.getState()).map.link.x += 1;
    	}
    	((ZeldaMMGameState)handler.getState()).map.link.bounds.x = ((ZeldaMMGameState)handler.getState()).map.link.x;
    	((ZeldaMMGameState)handler.getState()).map.link.bounds.y = ((ZeldaMMGameState)handler.getState()).map.link.y;
    	((ZeldaMMGameState)handler.getState()).map.link.changeIntersectingBounds();
    	moveLinkHelper(dir, speed - 1);
    }
}
