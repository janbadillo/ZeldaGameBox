package Game.Zelda.Entities.Dynamic;

import Game.GameStates.Zelda.ZeldaGameState;
import Game.Zelda.Entities.Statics.DungeonDoor;
import Game.Zelda.Entities.Statics.SectionDoor;
import Game.Zelda.Entities.Statics.SolidStaticEntities;
import Main.Handler;
import Resources.Animation;
import Resources.Images;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * Created by AlexVR on 3/15/2020
 */
public class JumpSpider extends BaseMovingEntity {


	private final int animSpeed = 120;
	int newMapX=0,newMapY=0,xExtraCounter=0,yExtraCounter=0;
	Direction movingTo;
	public boolean dead = false,knockedBack = false, hitStun = false;
	public int hp = 2;
	private int count,hitStunCounter, knockBackX, knockBackY;


	public JumpSpider(int x, int y, BufferedImage[] sprite, Handler handler) {
		super(x, y, sprite, handler);
		speed = 15;
		health = 2;

		walkAnimation = new Animation(animSpeed,Images.bouncyEnemyFrames);

		handler.getZeldaGameState();


	}

	@Override
	public void tick() {
		updateAttackHitbox();
	
		if (count > 0) {
	    	count--;
	    }
	    if(count == 0) {
	    	direction = Direction.randomDir();
	    	count = 30;
	    	walkAnimation.tick();
	    	move(direction);
	    }
	    if (knockedBack) {
    		int knockBackSpeed = 10;
    		int errorRange = 10;
    		if (x > knockBackX - errorRange && x < knockBackX + errorRange && y > knockBackY - errorRange && y < knockBackY + errorRange) {
    			knockedBack = false;
    		} else {
    			if (!(x > knockBackX - errorRange && x < knockBackX + errorRange)) {
    				if (x < knockBackX) {
            			x += knockBackSpeed;
            		} else {
            			x -= knockBackSpeed;
            		}
    			}
    			if (!(y > knockBackY - errorRange && y < knockBackY + errorRange)) {
    				if (y < knockBackY) {
            			y += knockBackSpeed;
            		} else {
            			y -= knockBackSpeed;
            		}
    			}
            		
    		}
    		
	    }
	    if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_R)) {
			damage(1);
			System.out.print("ye");
		}
	
    ///CRASHES THE GAME, pero aja hope u get lo que queria hacer para que el enemy reciba damage 
    
    //if(Link.swordHitbox.intersects(getInteractBounds()) && Link.attacking == true) {
    //	damage(1);
    //}
    }
	
	@Override
    public void damage(int amount) {
		handler.getMusicHandler().playEffect("laser.wav");
		hp-=amount;
        if (hp<=0){
            dead = true;
        }
    }
	
	@Override
	public void knockBack(Direction hitDirection) {
    	int knockValue = 60;
    	knockBackHelper(hitDirection,knockValue);
    	
    }
    	
	@Override
	public void render(Graphics g) {
		if (moving) {
			g.drawImage(walkAnimation.getCurrentFrame(),x , y, width , height  , null);
		} else {
            g.drawImage(Images.cyclopSpider[0], x , y, width , height , null);
		}
		if(Handler.DEBUG) {
			g.drawRect((int)this.getInteractBounds().getX(), (int)this.getInteractBounds().getY(), (int)this.getInteractBounds().getWidth(), (int)this.getInteractBounds().getWidth());
			g.setColor(Color.red);
			g.drawRect(attackHitbox.x,attackHitbox.y,attackHitbox.width,attackHitbox.height);
			g.setColor(Color.GREEN);
			g.drawRect(bounds.x,bounds.y,bounds.width,bounds.height);
			g.setColor(Color.YELLOW);
		}
	}

	@Override
	public void move(Direction direction) {
		moving = true;
		changeIntersectingBounds();
		//check for collisions
		if (ZeldaGameState.inCave){

		}
		else {
			for (SolidStaticEntities objects : handler.getZeldaGameState().objects.get(handler.getZeldaGameState().mapX).get(handler.getZeldaGameState().mapY)) {
				if ((objects instanceof SectionDoor) && objects.bounds.intersects(bounds) && direction == ((SectionDoor) objects).direction) {
					if (!(objects instanceof DungeonDoor)) {

						
					}
					else {
						//if (((DungeonDoor) objects).name.equals("caveStartEnter")) {
						//	ZeldaGameState.inCave = true;
						//	handler.getZeldaGameState().toggle = false;
						//	x = ((DungeonDoor) objects).nLX;
						//	y = ((DungeonDoor) objects).nLY;
						//	direction = UP;
						//}
					}
				}
				else if (!(objects instanceof SectionDoor) && objects.bounds.intersects(interactBounds)) {
					//dont move
					return;
				}
			}
		}
		switch (direction) {
		case RIGHT:
			x += speed;
			break;
		case LEFT:
			x -= speed;

			break;
		case UP:
			y -= speed;
			break;
		case DOWN:
			y += speed;

			break;
		}
		bounds.x = x;
		bounds.y = y;
		changeIntersectingBounds();

	}
}
