package dev.codewizz.objects.ai;

import com.badlogic.gdx.math.Vector2;

import dev.codewizz.utils.Direction;
import dev.codewizz.utils.Utils;

public class Gene {

	private Direction dir;
	private int index;
	private float score = 0.5f;
	
	public Gene(Direction dir, int index) {
		this.dir = dir;
		this.index = index;
	}
	
	public Gene(int index) {
		this.index = index;
		
		if(Utils.RANDOM.nextBoolean()) {
			this.dir = Direction.Left;
		} else {
			this.dir = Direction.Right;
		}
	}
	
	public Vector2 getDir() {
		return this.dir.getDir();
	}
	
	public Direction getDirectionString() {
		return this.dir;
	}
	
	public float getScore() {
		return this.score;
	}
	
	public void setScore(float score) {
		this.score = score;
	}
	
	public int getIndex() {
		return this.index;
	}
	
}