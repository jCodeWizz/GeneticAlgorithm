package dev.codewizz.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import dev.codewizz.Main;
import dev.codewizz.physics2D.Rigidbody;
import dev.codewizz.physics2D.collision.CircleCollider;

public class Circle extends GameObject {

	private float radius;
	private Color color;
	
	public Circle(float x, float y, float radius) {
		super(x, y);
		this.id = ID.Circle;
		this.radius = radius;
		this.color = Color.WHITE;
		
		this.collider = new CircleCollider(this, radius);
		
		this.rigidbody = Rigidbody.createCircle(this, new Vector2(x, y), radius, 2f, false, 0.5f);
		
	}
	
	public Circle(float x, float y, float radius, Color color) {
		this(x, y, radius);
		this.color = color;
	}

	@Override
	public void update(float dt, int iterations) {
		super.update(dt, iterations);
	}

	@Override
	public void render(SpriteBatch b) {
		Main.shapeDrawer.filledCircle(this.rigidbody.getPosition(), radius, color);
		Main.shapeDrawer.circle(this.rigidbody.getPosition().x, this.rigidbody.getPosition().y, radius);
	}
}
