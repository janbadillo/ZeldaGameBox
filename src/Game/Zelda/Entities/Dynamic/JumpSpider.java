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
public class JumpSpider extends BaseMovingEntity {


	private final int animSpeed = 120;
	int newMapX=0,newMapY=0,xExtraCounter=0,yExtraCounter=0;
	Direction movingTo;
	public boolean dead = false;
	public int health = 2;


	public JumpSpider(int x, int y, BufferedImage[] sprite, Handler handler) {
		super(x, y, sprite, handler);
		speed = 4;
		health = 6;

		walkAnimation = new Animation(animSpeed,sprite);

		handler.getZeldaGameState();


	}

	@Override
	public void tick() {
		if(moving) {
			if (direction != UP) {
				direction = UP;
				walkAnimation.tick();
				move(direction);
			}

			else if (direction != DOWN) {
				direction = DOWN;
				walkAnimation.tick();
				move(direction);
			}
			else if (direction != Direction.LEFT) {
				direction = Direction.LEFT;
				walkAnimation.tick();
				move(direction);
			}
			else if (direction != Direction.RIGHT) {

				walkAnimation.tick();
				move(direction);
			} else {
				moving = false;
			}
		}
	}

	@Override
	public void render(Graphics g) {
		if (moving) {
			g.drawImage(walkAnimation.getCurrentFrame(),x , y, width , height  , null);
		} else {
            g.drawImage(Images.cyclopSpider[0], x , y, width , height , null);
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
