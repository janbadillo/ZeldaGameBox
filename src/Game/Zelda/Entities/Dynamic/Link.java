package Game.Zelda.Entities.Dynamic;

import Game.GameStates.State;
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
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import static Game.GameStates.Zelda.ZeldaGameState.worldScale;
import static Game.Zelda.Entities.Dynamic.Direction.DOWN;
import static Game.Zelda.Entities.Dynamic.Direction.UP;

/**
 * Created by AlexVR on 3/15/2020
 */
public class Link extends BaseMovingEntity {


    private final int animSpeed = 120, attackSpeed = 40;
    int newMapX=0,newMapY=0,xExtraCounter=0,yExtraCounter=0;
    public boolean movingMap = false, attackAnim = false, dead = false, armed = false, hitStun = false;
	public static boolean attacking;
	public boolean pickingUp = false;
    Direction movingTo;
    public int maxHealth = 6;
    private int tempX, tempY,  // used for alligning link when attacking
                swordWidth, swordHeight, // for establishing sword hitbox width and height
                pickUpCounter = 5;// counter for item pick up animation
    private Animation attackAnimation,pickUpItem;
    public Rectangle swordHitbox, upBound, rightBound, leftBound, downBound;
    private BufferedImage pickedUpItemSprite; // image of item that will displayed when link picks it up
    

    public Link(int x, int y, BufferedImage[] sprite, Handler handler) {
        super(x, y, sprite, handler);
        speed = 4;
        health = 6;
        BufferedImage[] animList = new BufferedImage[2];
        animList[0] = sprite[4];
        animList[1] = sprite[5];

        new Animation(64,Images.linkAttackLeft);
        new Animation(64,Images.linkAttackRight);
        new Animation(64,Images.linkAttackUp);
        new Animation(64,Images.linkAttackDown);
        
        walkAnimation = new Animation(animSpeed,animList);
        attackAnimation = new Animation(attackSpeed,sprite);
        handler.getZeldaGameState();
		
        swordWidth = width/3;
        swordHeight = height;
        
        pickUpItem = new Animation(160,Images.linkPickUp);
    }

    @Override
    public void tick() {
    	if (hitStunCounter <= 0) {
    		hitStun = false;
    	} else {
    		hitStunCounter--;
    	}
        if (movingMap){ 
            switch (movingTo) {
                case RIGHT:
                    handler.getZeldaGameState().cameraOffsetX+=10;  ///FASTER CAMERA MOVEMENT
                    newMapX+=10;
                    if (xExtraCounter>0){
                        x+=5;
                        xExtraCounter-=5;
                        walkAnimation.tick();

                    }else{
                        x-=10;
                        if(newMapX>0) {
                        	newMapX=0;
                        }
                    }
                    break;
                case LEFT:
                    handler.getZeldaGameState().cameraOffsetX-=10;
                    newMapX-=10;
                    if (xExtraCounter>0){
                        x-=5;
                        xExtraCounter-=5;
                        walkAnimation.tick();

                    }else{
                        x+=10;
                        if(newMapX<0) {
                        	newMapX=0;
                        }
                    }
                    break;
                case UP:
                    handler.getZeldaGameState().cameraOffsetY-=10;
                    newMapY+=10;
                    if (yExtraCounter>0){
                        y-=5;
                        yExtraCounter-=5;
                        walkAnimation.tick();

                    }else{
                        y+=10;
                        if(newMapY>0) {
                        	newMapY=0;
                        }
                    }
                    break;
                case DOWN:
                    handler.getZeldaGameState().cameraOffsetY+=10;
                    newMapY-=10;
                    if (yExtraCounter>0){
                        y+=5;
                        yExtraCounter-=5;
                        walkAnimation.tick();
                    }else{
                        y-=10;
                        if(newMapY<0) {
                        	newMapY=0;
                        }
                    }
                    break;
            }
            bounds = new Rectangle(x,y,width,height);
            changeIntersectingBounds();
            
            if (newMapX == 0 && newMapY == 0){
                movingMap = false;
                movingTo = null;
                newMapX = 0;
                newMapY = 0;
            }
        }else {
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
        		
        		
        	}else if (attacking) {
        		int startWidth = walkAnimation.getCurrentFrame().getWidth()*worldScale;
				int startHeight = walkAnimation.getCurrentFrame().getHeight()*worldScale;
				y = tempY;
				x = tempX;
        		//width and height must updated so link doesn't look distorted
        		width = attackAnimation.getCurrentFrame().getWidth()*worldScale;
        		height = attackAnimation.getCurrentFrame().getHeight()*worldScale;
        		attackAnimation.tick();
        		
        		if (direction == Direction.UP) {
        			y -= height - startHeight;
        		} else if (direction == Direction.LEFT) {
        			x -= width - startWidth;
        		}
        		
        		if (attackAnimation.end) {
        			x = tempX;
        			y = tempY;
        			width = sprite.getWidth()*worldScale;
        			height = sprite.getHeight()*worldScale;
        			attacking = false;
        			moving = true;
        			swordHitbox = null;
        		}
        	} else if (pickingUp) {
        		
        		
        		//////////// Un rebuluuuuuu ////////////
        		if (pickUpItem.getIndex() == 1) {
    				pickUpItem.setToLastFrame();
        			if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_ENTER)) {
        				pickingUp = false;
        				pickedUpItemSprite = null;
        				moving = true;
        			}
        		} else {
        			pickUpItem.setIndex(0);
        			if (pickUpCounter > 0) {
        				pickUpCounter--;
        			} else {
        				pickUpItem.setIndex(1);
        				pickUpCounter = 10;
        			}
        		}
        		////////////// Hopefully can fix...///////////
        	}
        	
        	else {
        		if (armed && !pickingUp) {
                	if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_ENTER) && !attacking) {
                		attack();
                    }
                }
                if (handler.getKeyManager().up) {
                    if (direction != UP) {
                        BufferedImage[] animList = new BufferedImage[2];
                        animList[0] = sprites[4];
                        animList[1] = sprites[5];
                        walkAnimation = new Animation(animSpeed, animList);
                        direction = UP;
                        //sprite = sprites[4];
                    }
                    walkAnimation.tick();
                    move(direction);

                } else if (handler.getKeyManager().down) {
                    if (direction != DOWN) {
                        BufferedImage[] animList = new BufferedImage[2];
                        animList[0] = sprites[0];
                        animList[1] = sprites[1];
                        walkAnimation = new Animation(animSpeed, animList);
                        direction = DOWN;
                        //sprite = sprites[0];
                    }
                    walkAnimation.tick();
                    move(direction);
                } else if (handler.getKeyManager().left) {
                    if (direction != Direction.LEFT) {
                        BufferedImage[] animList = new BufferedImage[2];
                        animList[0] = Images.flipHorizontal(sprites[2]);
                        animList[1] = Images.flipHorizontal(sprites[3]);
                        walkAnimation = new Animation(animSpeed, animList);
                        direction = Direction.LEFT;
                        //sprite = Images.flipHorizontal(sprites[3]);
                    }
                    walkAnimation.tick();
                    move(direction);
                } else if (handler.getKeyManager().right) {
                    if (direction != Direction.RIGHT) {
                        BufferedImage[] animList = new BufferedImage[2];
                        animList[0] = (sprites[2]);
                        animList[1] = (sprites[3]);
                        walkAnimation = new Animation(animSpeed, animList);
                        direction = Direction.RIGHT;
                        //sprite = (sprites[3]);
                    }
                    walkAnimation.tick();
                    move(direction);
                } else {
                    moving = false;
                }
        	}

        }
        
        if(ZeldaGameState.haveSword) {
        	armed = true;
        }

        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_1)) { //goes into debug mode for cheats
			Handler.DEBUG = !Handler.DEBUG;
        }
        
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_H) && health < maxHealth && Handler.DEBUG) {
        	health++;
        	handler.getMusicHandler().playEffect("getHeart.wav");
        //IF H IS PRESSED W/ DEBUG MODE ON health--CS
        }
        
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_J) && Handler.DEBUG) {
        	damage(1);
        }
        
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_Y) && maxHealth != 20 && Handler.DEBUG) {
        	maxHealth += 2;
        	handler.getMusicHandler().playEffect("getHeart.wav");
        //press Y to increase maximum hearts
        }
        
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_U) && maxHealth != 6 && Handler.DEBUG) {
        	if (health == maxHealth) {
        		health -= 2;
        	}
        	maxHealth -= 2;
        	handler.getMusicHandler().playEffect("linkHurt.wav");
        //press T to decrease maximum hearts (minimum is 6 hearts)
        }
        if (!attacking) {
        	updateHitbox();
        }
    }
    @Override
    public void damage(int amount) {
    	hitStun = true;
    	hitStunCounter = 30;
    	health -= amount;
    	handler.getMusicHandler().playEffect("linkHurt.wav");
    	if(health==0) {
    		handler.getMusicHandler().triggerGameOver();
    		State.setState(handler.getEndGameState());    //"We're in the EndGame Now" -DR Strange
    	}
    }
    public static void continueReset() {
    	health = 4;
    }
    
    
    private void updateHitbox() {
    	int cornerBox = width/6; // Hypothetical width and height of an empty square inside link's bounds' corners.
    	upBound = new Rectangle(x + cornerBox, y, width - cornerBox*2, cornerBox);
    	downBound = new Rectangle(x + cornerBox, y + height - cornerBox, width - cornerBox*2, cornerBox);
    	leftBound = new Rectangle(x, y + cornerBox, cornerBox, height - cornerBox*2);
    	rightBound = new Rectangle(x + width - cornerBox, y + cornerBox, cornerBox, height - cornerBox*2);
    }
    
    private void attack() {
    	moving = false;
    	tempX = x;
    	tempY = y;
    	attacking = true;
    	handler.getMusicHandler().playEffect("strongAttack.wav");
    	
    	if(direction == Direction.RIGHT) {
    		attackAnimation = new Animation(attackSpeed,Images.linkAttackRight);
    		swordHitbox = new Rectangle(tempX + width, tempY + width/2 - swordWidth/2, swordHeight, swordWidth);
    	} else if (direction == Direction.LEFT) {
    		attackAnimation = new Animation(attackSpeed,Images.linkAttackLeft);
    		swordHitbox = new Rectangle(tempX - swordHeight, tempY + width/2 - swordWidth/2, swordHeight, swordWidth);
    	} else if(direction == Direction.UP) {
    		attackAnimation = new Animation(attackSpeed,Images.linkAttackUp);
    		swordHitbox = new Rectangle(tempX + width/2 - swordWidth/2, y - swordHeight, swordWidth, swordHeight);
    	} else {
    		attackAnimation = new Animation(attackSpeed,Images.linkAttackDown);
    		swordHitbox = new Rectangle(tempX + width/2 - swordWidth/2, tempY + height, swordWidth, swordHeight);
    	}
    	
    }
    
    public void pickUpItem (BufferedImage a) { // Pick Up method needs the image of the item to be displayed above him.
    	moving = false;
    	pickedUpItemSprite = a;
    	pickingUp = true;
    }
    
    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
    
    @Override
    public void render(Graphics g) {
    	
    	BufferedImage image = null;
    	if (attacking) {
    		image = deepCopy(attackAnimation.getCurrentFrame());
    	} else{
    		image = deepCopy(walkAnimation.getCurrentFrame());
    	}
    	if (hitStun && image != null) {
    		if (hitStunCounter % 2 == 0) {
    			for(int i = 0; i < image.getWidth();i++)
                    for(int j = 0; j < image.getHeight(); j ++) {
                    	if (image.getRGB(i, j) == -8335344) {
                    		image.setRGB(i,j , -16777216);
                    	}else if (image.getRGB(i, j) == -3650548) {
                    		image.setRGB(i,j , -2611200);
                    	}else if (image.getRGB(i, j) == -223176) {
                    		image.setRGB(i,j , -16744312);
                    	}
                    }
    		} else{
    			for(int i = 0; i < image.getWidth();i++)
                    for(int j = 0; j < image.getHeight(); j ++) {
                    	if (image.getRGB(i, j) == -8335344) {
                    		image.setRGB(i,j , -197380);
                    	} else if (image.getRGB(i, j) == -3650548) {
                    		image.setRGB(i,j , -14665492);
                    	} else if (image.getRGB(i, j) == -223176) {
                    		image.setRGB(i,j , 16777215);
                    	}
                    }
    		}
    	}
    	
    	
    	if(attacking) {
    		g.drawImage(image,x , y, width , height  , null);
    	} else if (pickingUp) {
    		g.drawImage(pickUpItem.getCurrentFrame(),x , y, width , height  , null);
    		g.drawImage(pickedUpItemSprite,x + width/2 - swordWidth/2, y - swordHeight , swordWidth , swordHeight  , null);
    	} else if (moving) {
    		g.drawImage(image,x , y, width , height  , null);
    	} else {
    		if (movingMap){
                g.drawImage(walkAnimation.getCurrentFrame(),x , y, width, height  , null);
            }
            g.drawImage(image, x , y, width , height , null);
    	}
    	
    	if (Handler.DEBUG) {// For rendering things only when debug is on.
    		if(swordHitbox != null) {
    			g.drawRect(swordHitbox.x, swordHitbox.y, swordHitbox.width, swordHitbox.height);
    		}
    		g.drawRect(interactBounds.x, interactBounds.y, interactBounds.width, interactBounds.height);
    		g.setColor(Color.BLUE);
    		g.drawRect(upBound.x, upBound.y, upBound.width, upBound.height);
    		g.drawRect(downBound.x, downBound.y, downBound.width, downBound.height);
    		g.drawRect(leftBound.x, leftBound.y, leftBound.width, leftBound.height);
    		g.drawRect(rightBound.x, rightBound.y, rightBound.width, rightBound.height);
    	}
        
    }
    
    @Override
    public void move(Direction direction) {
        moving = true;
        changeIntersectingBounds();
        //chack for collisions
        if (ZeldaGameState.inCave){
            for (SolidStaticEntities objects : handler.getZeldaGameState().caveObjects) {
                if ((objects instanceof DungeonDoor) && objects.bounds.intersects(bounds) && direction == ((DungeonDoor) objects).direction) {
                    if (((DungeonDoor) objects).name.equals("caveStartLeave")) {
                        ZeldaGameState.inCave = false;
                        handler.getZeldaGameState().toggle = true;
                        x = ((DungeonDoor) objects).nLX;
                        y = ((DungeonDoor) objects).nLY;
                        direction = DOWN;
                    }
                } else if (!(objects instanceof DungeonDoor) && objects.bounds.intersects(interactBounds)) {
                    //dont move
                    return;
                }
            }
        }
        else {
            for (SolidStaticEntities objects : handler.getZeldaGameState().objects.get(handler.getZeldaGameState().mapX).get(handler.getZeldaGameState().mapY)) {
                if ((objects instanceof SectionDoor) && objects.bounds.intersects(bounds) && direction == ((SectionDoor) objects).direction) {
                    if (!(objects instanceof DungeonDoor)) {
                        movingMap = true;
                        movingTo = ((SectionDoor) objects).direction;
                        switch (((SectionDoor) objects).direction) {
                            case RIGHT:
                                newMapX = -(((handler.getZeldaGameState().mapWidth) + 1) * worldScale);
                                newMapY = 0;
                                handler.getZeldaGameState().mapX++;
                                xExtraCounter = 8 * worldScale + (2 * worldScale);
                                break;
                            case LEFT:
                                newMapX = (((handler.getZeldaGameState().mapWidth) + 1) * worldScale);
                                newMapY = 0;
                                handler.getZeldaGameState().mapX--;
                                xExtraCounter = 8 * worldScale + (2 * worldScale);
                                break;
                            case UP:
                                newMapX = 0;
                                newMapY = -(((handler.getZeldaGameState().mapHeight) + 1) * worldScale);
                                handler.getZeldaGameState().mapY--;
                                yExtraCounter = 8 * worldScale + (2 * worldScale);
                                break;
                            case DOWN:
                                newMapX = 0;
                                newMapY = (((handler.getZeldaGameState().mapHeight) + 1) * worldScale);
                                handler.getZeldaGameState().mapY++;
                                yExtraCounter = 8 * worldScale + (2 * worldScale);
                                break;
                        }
                        return;
                    }
                    else {
                        if (((DungeonDoor) objects).name.equals("caveStartEnter")) {
                            ZeldaGameState.inCave = true;
                            handler.getZeldaGameState().toggle = false;
                            x = ((DungeonDoor) objects).nLX;
                            y = ((DungeonDoor) objects).nLY;
                            direction = UP;
                        }
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
