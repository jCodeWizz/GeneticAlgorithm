package dev.codewizz.utils;

import com.badlogic.gdx.math.Vector2;

public enum Direction {
	
	Left(-1, 0),
	Right(1, 0),
	Up(0, 1),
	Down(0, -1);
	
	Vector2 dir;
	
	Direction(float dx, float dy) {
		dir = new Vector2(dx, dy);
	}
	
	public Vector2 getDir() {
		return dir;
	}
}
