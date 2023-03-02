package dev.codewizz.utils;

import com.badlogic.gdx.math.Vector2;

public class JMath {

	public static final float EPSILON = 0.0005f;
	
	public static float clamp(float value, float min, float max) {
		if(min == max) { return min; }
		if(min > max) Debug.throwStateError("JMath.clamp: min > max (Unclampable)");
		
		if(value < min) 
			return min;
		
		if(value > max)
			return max;
		
		return value;
	}
	
	public static boolean nearlyEqual(float a, float b) {
		return Math.abs(a - b) < EPSILON;
	}
	
	public static boolean nearlyEqual(Vector2 a, Vector2 b) {
		return a.dst2(b) < EPSILON * EPSILON;
	}
}
