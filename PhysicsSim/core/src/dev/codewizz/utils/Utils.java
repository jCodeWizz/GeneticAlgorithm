package dev.codewizz.utils;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Utils {

	public static Random RANDOM = new Random();
	
	public static Color getRandomColor() {
		float r = RANDOM.nextFloat();
		float g = RANDOM.nextFloat();
		float b = RANDOM.nextFloat();
		
		return new Color(r, g, b, 1f);
	}
	
	public static Vector2 rotate(Vector2 v, float angleRadian, Vector2 origin) {
		Vector2 vec = new Vector2(v);
		
		
		float x = vec.x - origin.x;
		float y = vec.y - origin.y;
		
		float cos = (float)Math.cos(angleRadian);
		float sin = (float)Math.sin(angleRadian);
		
		float xPrime = (x * cos) - (y * sin);
		float yPrime = (x * sin) + (y * cos);
		
		xPrime += origin.x;
		yPrime += origin.y;
		
		vec.x = xPrime;
		vec.y = yPrime;
		
		return vec;
	}
}
