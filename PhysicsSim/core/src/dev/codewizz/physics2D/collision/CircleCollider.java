package dev.codewizz.physics2D.collision;

import com.badlogic.gdx.math.Vector2;

import dev.codewizz.objects.GameObject;

public class CircleCollider extends Collider {

	private float radius = 1f;
	
	public CircleCollider(GameObject object, float radius) {
		this.object = object;
		this.radius = radius;
	}
	
	public Vector2 getCenter() {
		return this.object.getRigidbody().getPosition();
	}

	public float getRadius() {
		return radius;
	}

	public GameObject getObject() {
		return object;
	}
}
