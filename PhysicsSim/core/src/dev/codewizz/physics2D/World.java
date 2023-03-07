package dev.codewizz.physics2D;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import dev.codewizz.Main;
import dev.codewizz.objects.Agent;
import dev.codewizz.objects.Box;
import dev.codewizz.objects.GameObject;
import dev.codewizz.objects.ID;
import dev.codewizz.objects.ai.Gene;
import dev.codewizz.physics2D.collision.AABB;
import dev.codewizz.physics2D.collision.CollisionResult;
import dev.codewizz.physics2D.collision.Collisions;
import dev.codewizz.utils.Utils;

public class World {

	public static final float minBodySize = 0.1f * 0.1f;
	public static final float maxBodySize = 640f * 640f;

	public static final float minDensity = 0.2f;
	public static final float maxDensity = 21.4f;

	public List<GameObject> objects = new CopyOnWriteArrayList<>();
	public List<CollisionResult> results = new CopyOnWriteArrayList<>();
	public List<Vector2> points = new CopyOnWriteArrayList<>();

	public int cycles = 0;
	public float best = 0;
	public float diff = 0;
	public Vector2 bestPos = new Vector2();

	public World() {

		Box ground = new Box(0, -250, 5000f, 50f, Utils.getRandomColor());
		ground.getRigidbody().setStatic(true);
		ground.setLayer(1000);
		this.addObject(ground);

		for (int i = 0; i < 50; i++) {
			Agent agent = new Agent(0f, -200f, 20);
			this.addObject(agent);
		}
	}

	public void update(float dt, int iterations) {

		points.clear();
		for (int it = 0; it < iterations; it++) {

			for (GameObject object : objects) {
				object.update(dt, iterations);
			}

			for (int i = 0; i < objects.size() - 1; i++) {
				Rigidbody b1 = objects.get(i).getRigidbody();
				AABB aabb1 = b1.getAABB();

				for (int j = i + 1; j < objects.size(); j++) {

					Rigidbody b2 = objects.get(j).getRigidbody();
					AABB aabb2 = b2.getAABB();

					if (b1.isStatic() && b2.isStatic())
						continue;

					if (!Collisions.AABBAndAABB(aabb1, aabb2))
						continue;

					if (b1.getObject().getLayer() == b2.getObject().getLayer())
						continue;

					CollisionResult result = Collisions.testCollision(b1.getObject().getCollider(),
							b2.getObject().getCollider());

					if (result.isIntersecting()) {
						seperateBodies(b1, b2, result);

						results.add(result);
						if (result.getContactPoints() != null) {
							points.addAll(result.getContactPoints());
						}
					}
				}
			}

			for (CollisionResult result : results) {
				resolveCollisionBasic(result);
			}
			results.clear();

		}

		for (GameObject object : objects) {
			if(object.getID() == ID.Agent) {
				((Agent) object).tick();
			}
			if (object.getRigidbody().getAABB().maxY < -300f && !object.getRigidbody().isStatic()) {
				this.removeObject(object);
			}
		}

		aiStep();
	}

	private Agent getBestAgent() {

		Agent best = null;
		float bestScore = -Float.MAX_VALUE;

		for (GameObject object : objects) {
			if (object.getID() == ID.Agent) {
				Agent agent = (Agent) object;
				if (agent.getScore() > bestScore) {
					bestScore = agent.getScore();
					best = agent;
				}
			}
		}

		return best;
	}

	private void aiStep() {

		if (!Agent.started) {

			cycles++;

			Agent bestAgent = getBestAgent();

			float lastBest = best;

			best = bestAgent.getScore();

			diff = best - lastBest;

			bestPos = new Vector2(bestAgent.getRigidbody().getPosition().x, 0);

			System.out.println("best: " + best);
			System.out.println("diff: " + diff);
			System.out.println("cycle: " + (cycles - 1));

			for (GameObject object : objects) {
				if (object.getID() == ID.Agent) {
					removeObject(object);
				}
			}
			
			
			
			for(int p = 0; p < 50; p++) {
				ArrayList<Gene> newGenes = new ArrayList<>();
				ArrayList<Gene> genes = bestAgent.getGeneStreng();
				for(int i = 0; i < Agent.length; i++) {
					if(i < genes.size()) {
						if(genes.get(i).getScore() < 0.5f) {
							newGenes.add(new Gene(i));
						} else {
							newGenes.add(new Gene(genes.get(i).getDirectionString(), i));
						}
					} else {
						newGenes.add(new Gene(genes.get(i).getDirectionString(), i));
					}
				}
				
				Agent newAgent = new Agent(0f, -200f, 20, newGenes);
				addObject(newAgent);
			}
			

			Agent.started = true;
			
		}
	}

	private void seperateBodies(Rigidbody b1, Rigidbody b2, CollisionResult result) {
		Vector2 normal = result.getNormal();
		float depth = result.getDepth();

		if (b1.isStatic()) {
			b2.move(new Vector2(normal).scl(depth));
		} else if (b2.isStatic()) {
			b1.move(new Vector2(normal).scl(-depth));
		} else {
			b1.move(new Vector2(normal).scl(-depth / 2f));
			b2.move(new Vector2(normal).scl(depth / 2f));
		}
	}

	public void resolveCollisionBasic(CollisionResult result) {

		Vector2 normal = new Vector2(result.getNormal());
		Rigidbody a = result.getBodyA().getRigidbody();
		Rigidbody b = result.getBodyB().getRigidbody();

		Vector2 relativeVelocity = new Vector2(b.getLinearVelocity()).sub(a.getLinearVelocity());

		if (relativeVelocity.dot(normal) > 0f) {
			return;
		}

		float e = Math.min(a.getRestitution(), b.getRestitution());

		float j = -(1f + e) * relativeVelocity.dot(normal);
		j /= a.getInvMass() + b.getInvMass();

		Vector2 impulse = normal.scl(j);

		a.getLinearVelocity().sub(new Vector2(impulse).scl(a.getInvMass()));
		b.getLinearVelocity().add(new Vector2(impulse).scl(b.getInvMass()));
	}

	// THE FUCKING PROBLEM IS HERE DAMMIT
	public void resolveCollisionWithRotation(CollisionResult result) {

		Rigidbody bodyA = result.getBodyA().getRigidbody();
		Rigidbody bodyB = result.getBodyB().getRigidbody();
		Vector2 normal = new Vector2(result.getNormal());
		List<Vector2> points = result.getContactPoints();
		List<Vector2> impulseList = new ArrayList<Vector2>();
		List<Vector2> raList = new ArrayList<Vector2>();
		List<Vector2> rbList = new ArrayList<Vector2>();

		float e = Math.min(bodyA.getRestitution(), bodyB.getRestitution());
		for (int i = 0; i < points.size(); i++) {
			Vector2 ra = new Vector2(points.get(i)).sub(bodyA.getPosition());
			Vector2 rb = new Vector2(points.get(i)).sub(bodyB.getPosition());

			raList.add(new Vector2(ra));
			rbList.add(new Vector2(rb));

			Vector2 raPerp = new Vector2(-ra.y, ra.x);
			Vector2 rbPerp = new Vector2(-rb.y, rb.x);

			Vector2 angularLinearVelocityA = new Vector2(raPerp).scl(bodyA.getAngularVelocity());
			Vector2 angularLinearVelocityB = new Vector2(rbPerp).scl(bodyB.getAngularVelocity());

			Vector2 vectorA = new Vector2(bodyA.getLinearVelocity()).add(angularLinearVelocityA);
			Vector2 vectorB = new Vector2(bodyB.getLinearVelocity()).add(angularLinearVelocityB);

			Vector2 relativeVelocity = new Vector2(vectorB).sub(vectorA);

			float contactVelocityMag = relativeVelocity.dot(normal);

			if (contactVelocityMag > 0f) {
				continue;
			}

			float raPerpDotN = raPerp.dot(normal);
			float rbPerpDotN = rbPerp.dot(normal);

			float denom = bodyA.getInvMass() + bodyB.getInvMass() + (raPerpDotN * raPerpDotN) * bodyA.getInvInertia()
					+ (rbPerpDotN * rbPerpDotN) * bodyB.getInvInertia();

			float j = -(1f + e) * contactVelocityMag;
			j /= denom;
			j /= (float) points.size();

			Vector2 impulse = normal.scl(j);
			impulseList.add(impulse);
		}

		for (int i = 0; i < impulseList.size(); i++) {
			Vector2 impulse = impulseList.get(i);

			Vector2 ra = raList.get(i);
			Vector2 rb = rbList.get(i);

			bodyA.getLinearVelocity().sub(new Vector2(impulse).scl(bodyA.getInvMass()));
			bodyA.setAngularVelocity(bodyA.getAngularVelocity() + -impulse.crs(ra) * bodyA.getInvInertia());
			bodyB.getLinearVelocity().add(new Vector2(impulse).scl(bodyB.getInvMass()));
			bodyB.setAngularVelocity(bodyB.getAngularVelocity() + impulse.crs(rb) * bodyB.getInvInertia());

		}

	}

	public void render(SpriteBatch b) {
		for (GameObject object : objects) {
			object.render(b);
		}

		//for (Vector2 point : points) {
		//	Main.shapeDrawer.filledCircle(point, 2f, Color.RED);
		//}

		Main.shapeDrawer.filledCircle(bestPos, 2f, Color.RED);
	}

	public void addObject(GameObject object) {
		objects.add(object);
	}

	public void removeObject(GameObject object) {
		objects.remove(object);
		object.destroy();
	}

	public GameObject getObject(int index) {
		return objects.get(index);
	}
}
