package dev.codewizz.objects.ai;

import dev.codewizz.utils.Debug;
import dev.codewizz.utils.Utils;

public class Gene {

	private Action action;
	private int index;
	private float score = 0.5f;
	
	public Gene(Action action, int index) {
		this.action = action;
		this.index = index;
	}
	
	public Gene(int index) {
		this.index = index;
		
		int r = Utils.RANDOM.nextInt(3);
		
		if(r == 0) {
			this.action = Action.Left;
		} else if (r == 1){
			this.action = Action.Right;
		} else if(r == 2) {
			this.action = Action.Jump;
		} else {
			Debug.error("GENE HAS MORE RANDOM THAN ACTIONS");
		}
	}
	
	public Action getAction() {
		return this.action;
	}
	
	public float getScore() {
		return this.score;
	}
	
	public void setScore(float score) {
		this.score = score;
	}
	
	public int getIndex() {
		return this.index;
	}
	
}