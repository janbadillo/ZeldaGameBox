package Game.Zelda.Entities.Dynamic;

import java.util.Random;

/**
 * Created by AlexVR on 3/15/2020
 */
public enum Direction {
	LEFT("Left"),
	RIGHT("Right"),
	UP("Up"),
	DOWN("Down");
	int count;
	static Random rand = new Random();
	private String direction;
	private static Direction dir;
	static int x;

	Direction(String direction) {
		this.direction = direction;
	}

	public String getDirection() {
		return this.direction;
	}
	public static Direction randomDir() {
		x = rand.nextInt(4);
		if(x == 0) {
			dir = LEFT;
		}else if(x == 1) {
			dir = RIGHT;
		}else if(x == 2) {
			dir = UP;
		}else if(x == 3) {
			dir = DOWN;
		}
		return dir;

	}

	public static Direction getDirection(String direction) {
		return Direction.valueOf(direction.toUpperCase());
	}
}
