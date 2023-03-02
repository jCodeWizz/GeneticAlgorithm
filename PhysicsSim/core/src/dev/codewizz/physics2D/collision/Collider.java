package dev.codewizz.physics2D.collision;

import dev.codewizz.objects.GameObject;

public abstract class Collider {

	protected GameObject object;
	
	public void destroy() {
		this.object = null;
	}
	
	public GameObject getObject() {
		return object;
	}
}
