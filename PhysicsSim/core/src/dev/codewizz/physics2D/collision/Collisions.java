package dev.codewizz.physics2D.collision;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import dev.codewizz.utils.JMath;

public class Collisions {

	public static CollisionResult testCollision(Collider a, Collider b) {
		if (a instanceof BoxCollider) {
			if (b instanceof BoxCollider) {
				return polygonAndPolygon((BoxCollider) a, (BoxCollider) b);
			} else {
				CollisionResult result = circleAndPolygon((CircleCollider) b, (BoxCollider) a);
				Vector2 n = new Vector2(result.getNormal()).scl(-1);
				result.set(a.getObject(), b.getObject(), result.getDepth(), n, result.getContactPoints());
				return result;
			}
		} else {
			if (b instanceof BoxCollider) {
				return circleAndPolygon((CircleCollider) a, (BoxCollider) b);
			} else {
				return circleAndCircle((CircleCollider) a, (CircleCollider) b);
			}
		}
	}

	public static List<Vector2> findContactPoints(BoxCollider boxA, BoxCollider boxB) {
		ArrayList<Vector2> list = new ArrayList<>();

		Vector2[] verticesA = boxA.getTransformedVertices();
		Vector2[] verticesB = boxB.getTransformedVertices();
		float minDistSq = Float.MAX_VALUE;
		Vector2 contact1 = new Vector2();
		Vector2 contact2 = new Vector2();
		int contactCount = 0;

		for (int i = 0; i < verticesA.length; i++) {

			Vector2 p = verticesA[i];

			for (int j = 0; j < verticesB.length; j++) {
				Vector2 point = new Vector2();
				Vector2 a = verticesB[j];
				Vector2 b = verticesB[(j + 1) % verticesB.length];

				Vector2 ab = new Vector2(b).sub(a);
				Vector2 ap = new Vector2(p).sub(a);

				float proj = ap.dot(ab);
				float abLenSq = ab.len2();
				float d = proj / abLenSq;

				if (d <= 0f) {
					point = new Vector2(a);
				} else if (d >= 1f) {
					point = new Vector2(b);
				} else {
					point = new Vector2(ab).scl(d);
					point.add(a);
				}

				float distSq = p.dst2(point);

				if (JMath.nearlyEqual(distSq, minDistSq)) {
					if (!JMath.nearlyEqual(point, contact1)) {
						contact2 = new Vector2(point);
						contactCount = 2;
					}
				} else if (distSq < minDistSq) {
					minDistSq = distSq;
					contactCount = 1;
					contact1 = new Vector2(point);
				}
			}
		}

		for (int i = 0; i < verticesB.length; i++) {

			Vector2 p = verticesB[i];

			for (int j = 0; j < verticesA.length; j++) {
				Vector2 point = new Vector2();
				Vector2 a = verticesA[j];
				Vector2 b = verticesA[(j + 1) % verticesA.length];

				Vector2 ab = new Vector2(b).sub(a);
				Vector2 ap = new Vector2(p).sub(a);

				float proj = ap.dot(ab);
				float abLenSq = ab.len2();
				float d = proj / abLenSq;

				if (d <= 0f) {
					point = new Vector2(a);
				} else if (d >= 1f) {
					point = new Vector2(b);
				} else {
					point = new Vector2(ab).scl(d);
					point.add(a);
				}

				float distSq = p.dst2(point);

				if (JMath.nearlyEqual(distSq, minDistSq)) {
					if (!JMath.nearlyEqual(point, contact1)) {
						contact2 = new Vector2(point);
						contactCount = 2;
					}
				} else if (distSq < minDistSq) {
					minDistSq = distSq;
					contactCount = 1;
					contact1 = new Vector2(point);
				}
			}
		}

		if (contactCount == 1) {
			list.add(contact1);
		} else {
			list.add(contact2);
			list.add(contact1);
		}
		return list;
	}

	public static List<Vector2> findContactPoints(CircleCollider circleA, BoxCollider boxB) {
		ArrayList<Vector2> list = new ArrayList<>();

		float minDistSq = Float.MAX_VALUE;
		Vector2[] vertices = boxB.getTransformedVertices();
		Vector2 cp = new Vector2();

		for (int i = 0; i < vertices.length; i++) {
			Vector2 point = new Vector2();

			Vector2 p = new Vector2(circleA.getCenter());
			Vector2 a = vertices[i];
			Vector2 b = vertices[(i + 1) % vertices.length];

			Vector2 ab = new Vector2(b).sub(a);
			Vector2 ap = new Vector2(p).sub(a);

			float proj = ap.dot(ab);
			float abLenSq = ab.len2();
			float d = proj / abLenSq;

			if (d <= 0f) {
				point = new Vector2(a);
			} else if (d >= 1f) {
				point = new Vector2(b);
			} else {
				point = new Vector2(ab).scl(d);
				point.add(a);
			}

			float distanceSquared = p.dst2(point);

			if (distanceSquared < minDistSq) {
				minDistSq = distanceSquared;
				cp = new Vector2(point);
			}
		}
		list.add(cp);

		return list;
	}

	public static boolean AABBAndAABB(AABB a, AABB b) {
		return !(a.maxX <= b.minX || b.maxX <= a.minX || a.maxY <= b.minY || b.maxY <= a.minY);
	}

	public static List<Vector2> findContactPoints(CircleCollider a, CircleCollider b) {
		Vector2 ab = new Vector2(b.getCenter()).sub(a.getCenter());
		Vector2 dir = ab.nor();
		dir.scl(a.getRadius());
		Vector2 cp = new Vector2(a.getCenter()).add(dir);

		ArrayList<Vector2> list = new ArrayList<>();
		list.add(cp);
		return list;
	}

	public static CollisionResult circleAndPolygon(CircleCollider circle, BoxCollider box) {
		CollisionResult result = new CollisionResult();

		Vector2 normal = new Vector2();
		float depth = Float.MAX_VALUE;

		Vector2[] vertices = box.getTransformedVertices();
		Vector2 polygonCenter = box.getCenter();

		Vector2 axis = new Vector2();
		float axisDepth = 0f;
		float minA, maxA, minB, maxB;

		for (int i = 0; i < vertices.length; i++) {
			Vector2 va = vertices[i];
			Vector2 vb = vertices[(i + 1) % vertices.length];

			Vector2 edge = new Vector2(vb).sub(va);
			axis = new Vector2(-edge.y, edge.x);
			axis = axis.nor();

			Vector2 resultA = Collisions.projectVertices(vertices, axis);

			maxA = resultA.x;
			minA = resultA.y;

			Vector2 resultB = Collisions.projectCircle(circle, axis);

			maxB = resultB.x;
			minB = resultB.y;

			if (minA >= maxB || minB >= maxA) {
				result.reset();
				return result;
			}

			axisDepth = Math.min(maxB - minA, maxA - minB);

			if (axisDepth < depth) {
				depth = axisDepth;
				normal = axis;
			}
		}

		int cpIndex = Collisions.findClosestPointOnPolygon(circle, vertices);
		Vector2 cp = vertices[cpIndex];

		axis = new Vector2(cp).sub(circle.getCenter());
		axis = axis.nor();

		Vector2 resultA = Collisions.projectVertices(vertices, axis);

		maxA = resultA.x;
		minA = resultA.y;

		Vector2 resultB = Collisions.projectCircle(circle, axis);

		maxB = resultB.x;
		minB = resultB.y;

		if (minA >= maxB || minB >= maxA) {
			result.reset();
			return result;
		}

		axisDepth = Math.min(maxB - minA, maxA - minB);

		if (axisDepth < depth) {
			depth = axisDepth;
			normal = axis;
		}

		Vector2 direction = new Vector2(polygonCenter).sub(circle.getCenter());

		if (direction.dot(new Vector2(normal)) < 0f) {
			normal = normal.scl(-1);
		}

		List<Vector2> list = findContactPoints(circle, box);

		result.set(circle.getObject(), box.getObject(), depth, normal, list);
		return result;
	}

	public static CollisionResult circleAndCircle(CircleCollider a, CircleCollider b) {
		CollisionResult result = new CollisionResult();

		float depth = 0f;

		float distance = Vector2.dst(a.getCenter().x, a.getCenter().y, b.getCenter().x, b.getCenter().y);
		float radii = a.getRadius() + b.getRadius();

		if (distance >= radii) {
			result.reset();
			return result;
		}

		Vector2 normal = new Vector2(b.getCenter()).sub(a.getCenter()).nor();
		depth = radii - distance;
		result.set(a.getObject(), b.getObject(), depth, normal, findContactPoints(a, b));

		return result;
	}

	private static int findClosestPointOnPolygon(CircleCollider circle, Vector2[] vertices) {

		int result = -1;
		float minDistance = Float.MAX_VALUE;

		for (int i = 0; i < vertices.length; i++) {
			Vector2 v = vertices[i];

			float distance = v.dst(circle.getCenter());

			if (distance < minDistance) {
				minDistance = distance;
				result = i;
			}
		}

		return result;
	}

	private static Vector2 projectCircle(CircleCollider circle, Vector2 axis) {
		float min = 0f, max = 0f;

		Vector2 direction = new Vector2(axis).nor();
		Vector2 directionAndRadius = direction.scl(circle.getRadius());

		Vector2 p1 = new Vector2(circle.getCenter()).add(directionAndRadius);
		Vector2 p2 = new Vector2(circle.getCenter()).sub(directionAndRadius);

		min = p1.dot(axis);
		max = p2.dot(axis);

		if (min > max) {
			float t = min;
			min = max;
			max = t;
		}

		return new Vector2(max, min);
	}

	private static Vector2 projectVertices(Vector2[] vertices, Vector2 axis) {
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;

		for (int i = 0; i < vertices.length; i++) {
			Vector2 v = vertices[i];
			float proj = Vector2.dot(v.x, v.y, axis.x, axis.y);

			if (proj < min) {
				min = proj;
			}
			if (proj > max) {
				max = proj;
			}

		}

		return new Vector2(max, min);
	}

	public static CollisionResult polygonAndPolygon(BoxCollider a, BoxCollider b) {
		Vector2[] verticesA = a.getTransformedVertices();
		Vector2[] verticesB = b.getTransformedVertices();

		CollisionResult result = new CollisionResult();

		Vector2 normal = new Vector2();
		float depth = Float.MAX_VALUE;

		for (int i = 0; i < verticesA.length; i++) {
			Vector2 va = verticesA[i];
			Vector2 vb = verticesA[(i + 1) % verticesA.length];

			Vector2 edge = new Vector2(vb).sub(va);
			Vector2 axis = new Vector2(-edge.y, edge.x).nor();

			Vector2 projResultA = projectVertices(verticesA, axis);
			float minA = projResultA.y;
			float maxA = projResultA.x;

			Vector2 projResultB = projectVertices(verticesB, axis);
			float minB = projResultB.y;
			float maxB = projResultB.x;

			if (minA >= maxB || minB >= maxA) {
				result.reset();
				return result;
			}

			float axisDepth = Math.min(maxB - minA, maxA - minB);

			if (axisDepth < depth) {
				depth = axisDepth;
				normal = axis;
			}
		}

		for (int i = 0; i < verticesB.length; i++) {
			Vector2 va = verticesB[i];
			Vector2 vb = verticesB[(i + 1) % verticesB.length];

			Vector2 edge = new Vector2(vb).sub(va);
			Vector2 axis = new Vector2(-edge.y, edge.x).nor();

			Vector2 projResultA = projectVertices(verticesA, axis);
			float minA = projResultA.y;
			float maxA = projResultA.x;

			Vector2 projResultB = projectVertices(verticesB, axis);
			float minB = projResultB.y;
			float maxB = projResultB.x;

			if (minA >= maxB || minB >= maxA) {
				result.reset();
				return result;
			}

			float axisDepth = Math.min(maxB - minA, maxA - minB);

			if (axisDepth < depth) {
				depth = axisDepth;
				normal = axis;
			}
		}

		Vector2 centerA = Collisions.FindArithmeticMean(verticesA);
		Vector2 centerB = Collisions.FindArithmeticMean(verticesB);

		Vector2 direction = new Vector2(centerB).sub(centerA);

		if (direction.dot(normal) < 0f) {
			normal = normal.scl(-1);
		}

		result.set(a.getObject(), b.getObject(), depth, normal, findContactPoints(a, b));
		return result;

	}

	public static Vector2 FindArithmeticMean(Vector2[] vertices) {
		float sumX = 0f;
		float sumY = 0f;

		for (int i = 0; i < vertices.length; i++) {
			Vector2 v = vertices[i];
			sumX += v.x;
			sumY += v.y;
		}

		return new Vector2(sumX / (float) vertices.length, sumY / (float) vertices.length);
	}
}
