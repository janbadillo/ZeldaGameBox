package Game.GameStates.Zelda;

import Game.GameStates.State;
import Game.PacMan.entities.Statics.BaseStatic;
import Game.Zelda.Entities.Dynamic.BaseMovingEntity;
import Game.Zelda.Entities.Dynamic.Direction;
import Game.Zelda.Entities.Dynamic.JumpSpider;
import Game.Zelda.Entities.Dynamic.Link;
import Game.Zelda.Entities.Statics.DungeonDoor;
import Game.Zelda.Entities.Statics.SectionDoor;
import Game.Zelda.Entities.Statics.SolidStaticEntities;
import Game.Zelda.Entities.Statics.WalkingSolidEntities;
import Main.Handler;
import Resources.Animation;
import Resources.Images;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by AlexVR on 3/14/2020
 */
public class ZeldaGameState extends State {


	public static int xOffset,yOffset,stageWidth,stageHeight,worldScale;
	public static int cameraOffsetX;
	public static int cameraOffsetY;
	//map is 16 by 7 squares, you start at x=7,y=7 starts counting at 0
	public static int mapX;
	public static int mapY;
	public static int mapWidth;
	public static int mapHeight;
	public boolean toggle = true;
	public ArrayList<ArrayList<ArrayList<SolidStaticEntities>>> objects;
	public ArrayList<ArrayList<ArrayList<BaseMovingEntity>>> enemies;
	public static Link link;
	public JumpSpider spider;
	public static boolean haveSword = false;
	public static boolean inCave = false;
	public ArrayList<SolidStaticEntities> caveObjects;
	public ArrayList<WalkingSolidEntities> caveGrabbable;
	public int temp = 0;
	private Animation caveFire = new Animation(64,Images.npcFire);



	public ZeldaGameState(Handler handler) {
		super(handler);
		xOffset = handler.getWidth()/4;
		yOffset = handler.getHeight()/4;
		stageWidth = handler.getWidth()/3 + (handler.getWidth()/15);
		stageHeight = handler.getHeight()/2;
		///
		worldScale = 3;
		mapX = 7;
		mapY = 7;
		mapWidth = 256;
		mapHeight = 176;
		cameraOffsetX =  ((mapWidth*mapX) + mapX + 1)*worldScale;
		cameraOffsetY = ((mapHeight*mapY) + mapY + 1)*worldScale;

		objects = new ArrayList<>();
		enemies = new ArrayList<>();
		caveObjects = new ArrayList<>();
		caveGrabbable = new ArrayList<>();

		for (int i =0;i<16;i++){
			objects.add(new ArrayList<>());
			enemies.add(new ArrayList<>());
			for (int j =0;j<8;j++) {
				objects.get(i).add(new ArrayList<>());
				enemies.get(i).add(new ArrayList<>());
			}
		}

		addWorldObjects();

		link = new Link(xOffset+(stageWidth/2),yOffset + (stageHeight/2),Images.zeldaLinkFrames,handler);

	}



	@Override
	public void tick() {
		link.tick();
		if (inCave){
			caveFire.tick();
			if (!haveSword) {
				Rectangle caveSword = new Rectangle(850,530,25,40);
				if (link.getInteractBounds().intersects(caveSword)) {
					haveSword = true;
					link.pickUpItem(Images.sword);
					caveSword = null;
					handler.getMusicHandler().playEffect("linkNewItem.wav");
				}
			}
		}else {

			if (!link.movingMap) {
				ArrayList<BaseMovingEntity> remove = new ArrayList<>();
				for (SolidStaticEntities entity : objects.get(mapX).get(mapY)) {
					entity.tick();
				}
				for (BaseMovingEntity entity : enemies.get(mapX).get(mapY)) {
					entity.tick();
					if (entity.getAttackHitbox().intersects(link.upBound) || entity.getAttackHitbox().intersects(link.downBound) || entity.getAttackHitbox().intersects(link.leftBound) || entity.getAttackHitbox().intersects(link.rightBound)){
						if (!link.hitStun && !Link.attacking) {
							link.damage(1);
							if (entity.getInteractBounds().intersects(link.upBound)) {
								link.knockBack(Direction.UP);
							}
							if (entity.getInteractBounds().intersects(link.downBound)) {
								link.knockBack(Direction.DOWN);
							}
							if (entity.getInteractBounds().intersects(link.leftBound)) {
								link.knockBack(Direction.LEFT);
							}
							if (entity.getInteractBounds().intersects(link.rightBound)) {
								link.knockBack(Direction.RIGHT);
							}
						}
					}
					if(link.swordHitbox != null) {
						if(entity.bounds.intersects(link.swordHitbox)){
							entity.damage(1);
							entity.knockBack(link.direction);
						}
					}
					if (entity instanceof JumpSpider) {
						if (((JumpSpider) entity).dead) {
							remove.add(entity);
						}
					}
				}
				for (BaseMovingEntity removing: remove){
                    enemies.get(mapX).get(mapY).remove(removing);
                }
				
				/*
				if (spider.getAttackHitbox().intersects(link.swordHitbox)) { 
						spider.damage(1);
						if (link.direction == Direction.RIGHT) {
							spider.knockBack(Direction.RIGHT);
						}
						if (link.direction == Direction.UP) {
							spider.knockBack(Direction.UP);
						}
						if (link.direction == Direction.LEFT) {
							spider.knockBack(Direction.LEFT);
						}
						if (link.direction == Direction.DOWN) {
							spider.knockBack(Direction.DOWN);
						}
					}*/
				}
			}
		}
	


	@Override
	public void render(Graphics g) {
		if (inCave){
			for (SolidStaticEntities entity : caveObjects) {
				entity.render(g);
			}
			temp=1;
			g.setColor(Color.WHITE);
			g.setFont(new Font("TimesRoman", Font.BOLD, 32));
			g.drawString("  IT ' S  DANGEROUS  TO  GO",(3 * (ZeldaGameState.stageWidth/16)) + ZeldaGameState.xOffset,(2 * (ZeldaGameState.stageHeight/11)) + ZeldaGameState.yOffset+ ((16*worldScale)));
			g.drawString("  ALONE !   TAKE  THIS",(4 * (ZeldaGameState.stageWidth/16)) + ZeldaGameState.xOffset,(4 * (ZeldaGameState.stageHeight/11)) + ZeldaGameState.yOffset- ((16*worldScale)/2));
			g.drawImage(Images.oldMan,835,460,50,50,null);
			g.drawImage(caveFire.getCurrentFrame(),705,460,50,50,null);
			g.drawImage(caveFire.getCurrentFrame(),960,460,50,50,null);

			//DUNGEON MUSIC
			if(toggle == false) {
				handler.getMusicHandler().changeMusic("TempleTime.wav");
				toggle = true;
			}

			//displays the sword in cave
			if(haveSword) {

			}else{
				g.drawImage(Images.sword,850,530,25,40,null);
			}

			link.render(g);

		}else {
			//OVERWORLD MUSIC
			if(toggle==true) {
				handler.getMusicHandler().changeMusic("zeldaoverworld.wav");
				toggle = false;
			}

			g.drawImage(Images.zeldaMap, -cameraOffsetX + xOffset, -cameraOffsetY + yOffset, Images.zeldaMap.getWidth() * worldScale, Images.zeldaMap.getHeight() * worldScale, null);
			if (!link.movingMap) {
				for (SolidStaticEntities entity : objects.get(mapX).get(mapY)) {
					entity.render(g);
				}
				for (BaseMovingEntity entity : enemies.get(mapX).get(mapY)) {
					entity.render(g);
				}
			}
			link.render(g);

			g.setColor(Color.BLACK);
			g.fillRect(0, 0, xOffset, handler.getHeight());
			g.fillRect(xOffset + stageWidth, 0, handler.getWidth(), handler.getHeight());
			g.fillRect(0, 0, handler.getWidth(), yOffset);
			g.fillRect(0, yOffset + stageHeight, handler.getWidth(), handler.getHeight());
		}
		if (Handler.DEBUG==true) {
			g.setFont(new Font("TimesRoman", Font.PLAIN, 25));
			g.setColor(Color.YELLOW);
			g.drawString("debug mode",handler.getWidth()/2-handler.getWidth()/1000+310,550);
		}
		//HEALTH IMPLEMENTATION
		g.drawImage(Images.lifeTitle,480,170,170,30,null) ;


		for (int i = 0; i < link.maxHealth/2; i++) {
			g.drawImage(Images.linkHeart[0],480+30*i,220,25,25,null) ;
		}
		if (Link.health % 2 == 0) {
			for (int i = 0; i < Link.health/2; i++) {
				g.drawImage(Images.linkHeart[2],480+30*i,220,25,25,null) ;
			}
		} else {
			int i;
			for (i = 0; i < (Link.health - 1)/2; i++) {
				g.drawImage(Images.linkHeart[2],480+30*i,220,25,25,null);
			}
			g.drawImage(Images.linkHeart[1],480+30*i,220,25,25,null);
		}
	}

	public static void quitReset() {
		Link.health = 6;
		JumpSpider.health = 6;
		link.x = xOffset+(stageWidth/2);
		link.y= yOffset + (stageHeight/2);
		mapX = 7;
		mapY = 7;
		cameraOffsetX =  ((mapWidth*mapX) + mapX + 1)*worldScale;
		cameraOffsetY = ((mapHeight*mapY) + mapY + 1)*worldScale;
		link.armed = false;
		haveSword = false;
	}

	private void addWorldObjects() {
		//cave
		for (int i = 0;i < 16;i++){
			for (int j = 0;j < 11;j++) {
				if (i>=2 && i<=13 && j>=2 && j< 9 ) {
					continue;
				}else{
					if (j>=9){
						if (i>1 && i<14) {
							if ((i == 7 || i==8 )){
								continue;
							}else {
								caveObjects.add(new SolidStaticEntities(i, j, Images.caveTiles.get(2), handler));
							}
						}else{
							caveObjects.add(new SolidStaticEntities(i,j,Images.caveTiles.get(5),handler));
						}
					}else{
						caveObjects.add(new SolidStaticEntities(i,j,Images.caveTiles.get(5),handler));
					}
				}
			}
		}
		caveObjects.add(new DungeonDoor(7,9,16*worldScale*2,16*worldScale * 2,Direction.DOWN,"caveStartLeave",handler,(4 * (ZeldaGameState.stageWidth/16)) + ZeldaGameState.xOffset,(2 * (ZeldaGameState.stageHeight/11)) + ZeldaGameState.yOffset));

		//7,7
		ArrayList<SolidStaticEntities> solids = new ArrayList<>();
		ArrayList<BaseMovingEntity> monster = new ArrayList<>();
		monster.add(new JumpSpider(xOffset+(stageWidth/2),yOffset + (stageHeight/4), Images.bouncyEnemyFrames, handler));
		solids.add(new SectionDoor(0,5,16*worldScale,16*worldScale, Direction.LEFT,handler));
		solids.add(new SectionDoor(7,0,16*worldScale * 2,16*worldScale,Direction.UP,handler));
		solids.add(new DungeonDoor(4,1,16*worldScale,16*worldScale,Direction.UP,"caveStartEnter",handler,(7 * (ZeldaGameState.stageWidth/16)) + ZeldaGameState.xOffset,(9 * (ZeldaGameState.stageHeight/11)) + ZeldaGameState.yOffset));
		solids.add(new SectionDoor(15,5,16*worldScale,16*worldScale,Direction.RIGHT,handler));
		solids.add(new SolidStaticEntities(6,0,Images.forestTiles.get(2),handler));
		solids.add(new SolidStaticEntities(5,1,Images.forestTiles.get(5),handler));
		solids.add(new SolidStaticEntities(6,1,Images.forestTiles.get(6),handler));
		solids.add(new SolidStaticEntities(3,2,Images.forestTiles.get(6),handler));
		solids.add(new SolidStaticEntities(2,3,Images.forestTiles.get(6),handler));
		solids.add(new SolidStaticEntities(1,4,Images.forestTiles.get(6),handler));
		solids.add(new SolidStaticEntities(1,6,Images.forestTiles.get(3),handler));
		solids.add(new SolidStaticEntities(1,7,Images.forestTiles.get(5),handler));
		solids.add(new SolidStaticEntities(1,8,Images.forestTiles.get(5),handler));
		solids.add(new SolidStaticEntities(2,9,Images.forestTiles.get(2),handler));
		solids.add(new SolidStaticEntities(3,9,Images.forestTiles.get(2),handler));
		solids.add(new SolidStaticEntities(4,9,Images.forestTiles.get(2),handler));
		solids.add(new SolidStaticEntities(5,9,Images.forestTiles.get(2),handler));
		solids.add(new SolidStaticEntities(6,9,Images.forestTiles.get(2),handler));
		solids.add(new SolidStaticEntities(7,9,Images.forestTiles.get(2),handler));
		solids.add(new SolidStaticEntities(8,9,Images.forestTiles.get(2),handler));
		solids.add(new SolidStaticEntities(9,9,Images.forestTiles.get(2),handler));
		solids.add(new SolidStaticEntities(10,9,Images.forestTiles.get(2),handler));
		solids.add(new SolidStaticEntities(11,9,Images.forestTiles.get(2),handler));
		solids.add(new SolidStaticEntities(12,9,Images.forestTiles.get(2),handler));
		solids.add(new SolidStaticEntities(13,9,Images.forestTiles.get(2),handler));
		solids.add(new SolidStaticEntities(14,8,Images.forestTiles.get(5),handler));
		solids.add(new SolidStaticEntities(14,7,Images.forestTiles.get(5),handler));
		solids.add(new SolidStaticEntities(14,6,Images.forestTiles.get(2),handler));
		solids.add(new SolidStaticEntities(14,4,Images.forestTiles.get(5),handler));
		solids.add(new SolidStaticEntities(13,4,Images.forestTiles.get(5),handler));
		solids.add(new SolidStaticEntities(12,4,Images.forestTiles.get(5),handler));
		solids.add(new SolidStaticEntities(11,4,Images.forestTiles.get(5),handler));
		solids.add(new SolidStaticEntities(10,4,Images.forestTiles.get(5),handler));
		solids.add(new SolidStaticEntities(9,4,Images.forestTiles.get(4),handler));
		solids.add(new SolidStaticEntities(9,3,Images.forestTiles.get(5),handler));
		solids.add(new SolidStaticEntities(9,2,Images.forestTiles.get(5),handler));
		solids.add(new SolidStaticEntities(9,1,Images.forestTiles.get(5),handler));
		solids.add(new SolidStaticEntities(9,0,Images.forestTiles.get(5),handler));
		objects.get(7).set(7,solids);
		enemies.get(7).set(7, monster);

		//6,7
		monster = new ArrayList<>();
		solids = new ArrayList<>();
		solids.add(new SectionDoor( 0,2,16*worldScale,16*worldScale*7, Direction.LEFT,handler));
		solids.add(new SectionDoor( 12,0,16*worldScale * 2,16*worldScale,Direction.UP,handler));
		solids.add(new SectionDoor( 15,5,16*worldScale,16*worldScale,Direction.RIGHT,handler));
		objects.get(6).set(7,solids);


		//7,6
		monster = new ArrayList<>();
		solids = new ArrayList<>();
		solids.add(new SectionDoor( 0,4,16*worldScale,16*worldScale*3, Direction.LEFT,handler));
		solids.add(new SectionDoor( 7,10,16*worldScale * 2,16*worldScale,Direction.DOWN,handler));
		solids.add(new SectionDoor( 15,4,16*worldScale,16*worldScale*3,Direction.RIGHT,handler));
		objects.get(7).set(6,solids);

		//8,7
		monster = new ArrayList<>();
		solids = new ArrayList<>();
		solids.add(new SectionDoor( 0,5,16*worldScale,16*worldScale, Direction.LEFT,handler));
		solids.add(new SectionDoor( 2,0,16*worldScale * 13,16*worldScale,Direction.UP,handler));
		solids.add(new SectionDoor( 15,2,16*worldScale,16*worldScale*7,Direction.RIGHT,handler));
		objects.get(8).set(7,solids);
	}

	@Override
	public void refresh() {

	}
}
