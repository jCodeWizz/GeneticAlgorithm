package dev.codewizz.physics2D;

import com.badlogic.gdx.math.Vector2;

import dev.codewizz.objects.GameObject;
import dev.codewizz.physics2D.collision.AABB;
import dev.codewizz.physics2D.collision.BoxCollider;
import dev.codewizz.physics2D.collision.CircleCollider;
import dev.codewizz.utils.Debug;
import dev.codewizz.utils.JMath;

public class Rigidbody {

	private GameObject object;
	
	private Vector2 position;
	private Vector2 linearVelocity;
	private Vector2 force;

	private float mass;
	private float density;
	private float restitution;
	private float angle;
	private float angularVelocity;
	private float area;
	private float inertia;
	private float invInertia;
	
	private boolean isStatic;
	private float invMass;

	private ShapeType shapeType;
	public boolean transformRequired;
	public boolean aabbRequired;
	
	public AABB aabb;
	
	private Rigidbody(GameObject object, Vector2 position, float density, float mass, float restitution, float area, boolean isStatic, ShapeType shapeType) {
		this.shapeType = shapeType;
		this.object = object;
		this.position = position;
		this.linearVelocity = new Vector2();
		this.force = new Vector2();
		this.isStatic = isStatic;
		
		
		this.angle = 0f;
		this.angularVelocity = 0f;
		
		this.density = density;
		this.mass = mass;
		this.restitution = restitution;
		this.area = area;
		
		this.inertia = this.calculateRotationalInertia();
		
		if(!this.isStatic) {
			this.invMass = 1f / this.mass;
			this.invInertia = 1f / this.inertia;
		} else {
			this.invMass = 0.0f;
			this.invInertia = 0.0f;
		}
		
		
		
		
		this.transformRequired = true;
		this.aabbRequired = true;
	}
	
	public void update(float dt, int iterations) {
		
		if(isStatic)
			return;
		
		
		dt /= iterations;
		
		this.addForce(new Vector2(0, -98.1f * mass));
		this.addForce(new Vector2(-0.5f * linearVelocity.x * area, -0.5f * linearVelocity.y * area));
		
		
		
		Vector2 acceleration = new Vector2(force).scl(1f/mass * dt);
		this.linearVelocity.add(new Vector2(acceleration));
		this.move(new Vector2(linearVelocity).scl(dt));
		
		this.angle += this.angularVelocity * dt;
		
		this.force = new Vector2();
		
		
	}
	
	private float calculateRotationalInertia() {
		if(shapeType == ShapeType.Circle) {
			float r = ((CircleCollider) this.object.getCollider()).getRadius();
			return (1f / 2f) * this.mass * r * r;
		} else if (shapeType == ShapeType.Box){
			float w = ((BoxCollider) this.object.getCollider()).getWidth();
			float h = ((BoxCollider) this.object.getCollider()).getHeight();
			return (1f / 12f) * this.mass * (w * w + h * h);
		} else {
			Debug.error("WRONG TYPE: " + shapeType.toString());
			return 0;
		}
	}
	
	public void move(Vector2 amount) {
		this.position.add(amount);
		this.transformRequired = true;
		this.aabbRequired = true;
	}
	
	public void moveTo(Vector2 pos) {
		this.position.set(pos);
		this.transformRequired = true;
		this.aabbRequired = true;
	}
	
	public void addForce(Vector2 force) {
		if(!isStatic) {
			this.force.add(force);
		}
	}
	
	public void rotate(float amount) {
		this.angle += amount;
		if(this.angle > 360) {
			this.angle -= 360;
		}
		this.transformRequired = true;
		this.aabbRequired = true;
	}
	
	public void setRotation(float amount) {
		this.angle = amount;
		if(this.angle > 360) {
			this.angle -= 360;
		}
		this.transformRequired = true;
		this.aabbRequired = true;
	}
	
	public AABB getAABB() {
		if(this.aabbRequired) {
			float minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE, minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
			
			if(this.shapeType == ShapeType.Box) {
				Vector2[] vertices = ((BoxCollider) this.object.getCollider()).getTransformedVertices();
				
				for(int i = 0; i < vertices.length; i++) {
					Vector2 v = vertices[i];
					
					if(v.x > maxX) maxX = v.x;
					if(v.x < minX) minX = v.x;
					if(v.y > maxY) maxY = v.y;
					if(v.y < minY) minY = v.y;
					
				}
			} else if(this.shapeType == ShapeType.Circle) {
				float radius = ((CircleCollider) this.object.getCollider()).getRadius();
				
				minX = this.getPosition().x - radius;
				minY = this.getPosition().y - radius;
				maxX = this.getPosition().x + radius;
				maxY = this.getPosition().y + radius;
				
				
			}
			
			this.aabb = new AABB(minX, minY, maxX, maxY);
		}
		
		return aabb;
	}
	
	public static Rigidbody createCircle(GameObject object, Vector2 position, float radius, float density, boolean isStatic, float restitution) {
		Rigidbody body = null;
		
		if(radius <= 0) { Debug.error("RADIUS {" + radius + "} IS NEGATIVE"); return null; }
		
		float area = radius * radius * (float)Math.PI;
		
		if(area > World.maxBodySize || area < World.minBodySize) { Debug.error("AREA {" + area + "} IS OUT OF BOUNDS"); return null; }
		if(density > World.maxDensity || density < World.minDensity) { Debug.error("DENSITY {" + density + "} IS OUT OF BOUNDS"); return null; }
		
		
		restitution = JMath.clamp(restitution, 0f, 1f);
		
		// mass = area * depth * density
		float mass = area * density;

		body = new Rigidbody(object, position, density, mass, restitution, area, isStatic, ShapeType.Circle);
		return body;
	}
	
	public static Rigidbody createBox(GameObject object, Vector2 position, float w, float h, float density, boolean isStatic, float restitution) {
		Rigidbody body = null;
		
		
		float area = w * h;
		if(area > World.maxBodySize || area < World.minBodySize) { Debug.error("AREA {" + area + "} IS OUT OF BOUNDS"); return null; }
		if(density > World.maxDensity || density < World.minDensity) { Debug.error("DENSITY {" + density + "} IS OUT OF BOUNDS"); return null; }
		
		restitution = JMath.clamp(restitution, 0f, 1f);
		
		// mass = area * depth * density
		float mass = area * density;
		
		body = new Rigidbody(object, position, density, mass, restitution, area, isStatic, ShapeType.Box);
		return body;
	}
	
	public GameObject getObject() {
		return this.object;
	}
	
	public Vector2 getPosition() {
		return this.position;
	}
	
	public float getAngle() {
		return this.angle;
	}
	
	public ShapeType getType() {
		return this.shapeType;
	}
	
	public float getMass() {
		return this.mass;
	}
	
	public Vector2 getLinearVelocity() {
		return linearVelocity;
	}

	public Vector2 getForce() {
		return force;
	}

	public float getDensity() {
		return density;
	}

	public float getRestitution() {
		return restitution;
	}

	public float getAngularVelocity() {
		return angularVelocity;
	}

	public float getArea() {
		return area;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public ShapeType getShapeType() {
		return shapeType;
	}

	public boolean isTransformRequired() {
		return transformRequired;
	}

	public float getInvMass() {
		if(isStatic) {
			return 0.0f;
		}
		return invMass;
	}
	
	public void destroy() {
		this.object = null;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	public float getInertia() {
		return inertia;
	}

	public float getInvInertia() {
		if(isStatic) {
			return 0.0f;
		}
		return invInertia;
	}

	public void setAngularVelocity(float angularVelocity) {
		this.angularVelocity = angularVelocity;
	}
}