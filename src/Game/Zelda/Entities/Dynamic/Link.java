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

import static Game.GameStates.Zelda.ZeldaGameState.worldScale;
import static Game.Zelda.Entities.Dynamic.Direction.DOWN;
import static Game.Zelda.Entities.Dynamic.Direction.UP;

/**
 * Created by AlexVR on 3/15/2020
 */
public class Link extends BaseMovingEntity {


    private final int animSpeed = 120, attackSpeed = 40;
    int newMapX=0,newMapY=0,xExtraCounter=0,yExtraCounter=0;
    public boolean movingMap = false, attackAnim = false;
    Direction movingTo;
    public boolean dead = false, armed = false, attacking;
    public int maxHealth = 6;
    private int tempX, tempY,  // used for alligning link when attacking
                swordWidth, swordHeight; // for establishing sword hitbox width and height;
    Animation attackAnimation,leftAttack,rightAttack,upAttack,downAttack,rightWalk,leftWalk,upWalk,downWalk;
    public Rectangle swordHitbox;
    

    public Link(int x, int y, BufferedImage[] sprite, Handler handler) {
        super(x, y, sprite, handler);
        speed = 4;
        health = 6;
        BufferedImage[] animList = new BufferedImage[2];
        animList[0] = sprite[4];
        animList[1] = sprite[5];

        leftAttack = new Animation(64,Images.linkAttackLeft);
        rightAttack = new Animation(64,Images.linkAttackRight);
        upAttack = new Animation(64,Images.linkAttackUp);
        downAttack = new Animation(64,Images.linkAttackDown);
        
        rightWalk = new Animation(64,Images.linkAttackLeft);
        //walkAnimation = new Animation(animSpeed,animList);
        attackAnimation = new Animation(attackSpeed,sprite);
        handler.getZeldaGameState();
		
        swordWidth = width/3;
        swordHeight = height;
    }

    @Override
    public void tick() {
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
        	if (attacking) {
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
        	} else {
                if (handler.getKeyManager().up) {
                    if (direction != UP) {
                        BufferedImage[] animList = new BufferedImage[2];
                        animList[0] = sprites[4];
                        animList[1] = sprites[5];
                        walkAnimation = new Animation(animSpeed, animList);
                        direction = UP;
                        sprite = sprites[4];
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
                        sprite = sprites[0];
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
                        sprite = Images.flipHorizontal(sprites[3]);
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
                        sprite = (sprites[3]);
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
        if (armed) {
        	if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_ENTER) && !attacking) {
        		attack();
            }
        }

        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_1)) { //goes into debug mode for cheats
			Handler.DEBUG = !Handler.DEBUG;
        }
        
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_H) && health < maxHealth && handler.DEBUG) {
        	health++;
        	handler.getMusicHandler().playEffect("getHeart.wav");
        //IF H IS PRESSED W/ DEBUG MODE ON health--CS
        }
        
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_J) && handler.DEBUG) {
        	dead=true;
        	health--;
        	handler.getMusicHandler().playEffect("linkHurt.wav");
        	if(health==0) {
        		handler.getMusicHandler().triggerGameOver();
        		State.setState(handler.getEndGameState());    //"We're in the EndGame Now" -DR Strange
        	}
        }
        
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_Y) && maxHealth != 20 && handler.DEBUG) {
        	maxHealth += 2;
        	handler.getMusicHandler().playEffect("getHeart.wav");
        //press Y to increase maximum hearts
        }
        
        if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_U) && maxHealth != 6 && handler.DEBUG) {
        	if (health == maxHealth) {
        		health -= 2;
        	}
        	maxHealth -= 2;
        	handler.getMusicHandler().playEffect("linkHurt.wav");
        //press T to decrease maximum hearts (minimum is 6 hearts)
        }
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
    
    @Override
    public void render(Graphics g) {
    	if(attacking) {
    		g.drawImage(attackAnimation.getCurrentFrame(),x , y, width , height  , null);
    	} else if (moving) {
    		g.drawImage(walkAnimation.getCurrentFrame(),x , y, width , height  , null);
    	} else {
    		if (movingMap){
                g.drawImage(walkAnimation.getCurrentFrame(),x , y, width, height  , null);
            }
            g.drawImage(sprite, x , y, width , height , null);
    	}
    	
    	if (swordHitbox != null && handler.DEBUG) {
    		g.drawRect(swordHitbox.x, swordHitbox.y, swordHitbox.width, swordHitbox.height);
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
