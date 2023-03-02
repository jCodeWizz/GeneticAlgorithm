package dev.codewizz.physics2D.collision;

import com.badlogic.gdx.math.Vector2;

import dev.codewizz.objects.GameObject;
import dev.codewizz.utils.Utils;

public class BoxCollider extends Collider {

	private float w, h;

	private Vector2[] vertices;
	private int[] triangles;
	private Vector2[] transformedVertices;

	public BoxCollider(GameObject object, float w, float h) {
		this.w = w;
		this.h = h;
		
		this.object = object;
		vertices = createBoxVertices(w, h);
		triangles = createBoxTriangles();
		transformedVertices = new Vector2[vertices.length];
	}

	private static Vector2[] createBoxVertices(float width, float height) {
		float left = -width / 2f;
		float right = left + width;
		float bottom = -height / 2f;
		float top = bottom + height;

		Vector2[] vertices = new Vector2[4];
		vertices[0] = new Vector2(left, top);
		vertices[1] = new Vector2(right, top);
		vertices[2] = new Vector2(right, bottom);
		vertices[3] = new Vector2(left, bottom);

		return vertices;
	}

	private static int[] createBoxTriangles() {
		int[] triangles = new int[6];
		triangles[0] = 0;
		triangles[1] = 1;
		triangles[2] = 2;
		triangles[3] = 0;
		triangles[4] = 2;
		triangles[5] = 3;
		return triangles;
	}
	
	public Vector2 getCenter() {
		return object.getRigidbody().getPosition();
	}

	public Vector2[] getTransformedVertices() {
		if (this.object.getRigidbody().transformRequired) {

			for (int i = 0; i < this.vertices.length; i++) {
				Vector2 v = this.vertices[i];
				
				Vector2 newVec = new Vector2(v).add(this.object.getRigidbody().getPosition());
				Vector2 origin = this.object.getRigidbody().getPosition();
				
				this.transformedVertices[i] = Utils.rotate(newVec, this.object.getRigidbody().getAngle(), origin);
			}

		}
		this.object.getRigidbody().transformRequired = false;
		return this.transformedVertices;
	}

	public int[] getTriangles() {
		return this.triangles;
	}

	public float getWidth() {
		return this.w;
	}

	public float getHeight() {
		return this.h;
	}
}
