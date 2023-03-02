package dev.codewizz.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import dev.codewizz.physics2D.Rigidbody;
import dev.codewizz.physics2D.collision.Collider;

public abstract class GameObject {

	protected Rigidbody rigidbody;
	protected Collider collider;
	protected int layer = 0;
	protected ID id;
	
	public GameObject(float x, float y) {
	}
	
	public void destroy() {
		rigidbody.destroy();
		collider.destroy();
		
		rigidbody = null;
		collider = null;
	}
	
	public void update(float dt, int iterations) {
		this.rigidbody.update(dt, iterations);
	}
	public abstract void render(SpriteBatch b);
	
	public Rigidbody getRigidbody() {
		return this.rigidbody;
	}
	
	public ID getID() {
		return this.id;
	}
	
	public Collider getCollider() {
		return this.collider;
	}
	
	public void move(Vector2 a) {
		this.rigidbody.move(a);
	}
	
	public void addForce(Vector2 force) {
		this.rigidbody.addForce(force);
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}
	
}
