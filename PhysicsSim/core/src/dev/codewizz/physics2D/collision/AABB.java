package dev.codewizz.physics2D.collision;

import com.badlogic.gdx.math.Vector2;

public class AABB {

	public float minX, minY, maxX, maxY;
	
	public AABB(Vector2 min, Vector2 max) {
		minX = min.x;
		minY = min.y;
		
		maxX = max.x;
		maxY = max.y;
	}
	
	public AABB(float minX, float minY, float maxX, float maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}
}
