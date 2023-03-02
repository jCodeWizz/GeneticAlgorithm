package dev.codewizz.objects;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

import dev.codewizz.objects.ai.Gene;
import dev.codewizz.utils.Direction;
import dev.codewizz.utils.Utils;

public class Agent extends Box {

	private static float movementSpeed = 5f;
	public static boolean started = true;
	public static int length = 500;

	private ArrayList<Gene> geneStreng = new ArrayList<Gene>();
	private int index = 0;
	private float score = 0.5f;
	private Vector2 firstPos;

	public Agent(float x, float y, float s) {
		super(x, y, s, s, Utils.getRandomColor());
		this.id = ID.Agent;
		init();
		firstPos = new Vector2(this.getRigidbody().getPosition());
	}

	public Agent(float x, float y, float s, ArrayList<Gene> genes) {
		super(x, y, s, s, Utils.getRandomColor());
		this.id = ID.Agent;
		this.geneStreng = genes;
		firstPos = new Vector2(this.getRigidbody().getPosition());
	}

	private void init() {
		for (int i = 0; i < length; i++) {
			geneStreng.add(new Gene(i));
		}
	}

	public void reset() {
		index = 0;
		this.getRigidbody().moveTo(firstPos);
	}

	public void tick() {
		
		Vector2 firstPos = new Vector2(this.rigidbody.getPosition());

		System.out.println(index);
		
		if (started) {
			Vector2 v = new Vector2(geneStreng.get(index).getDir()).scl(movementSpeed);
			this.rigidbody.move(v);
			index++;

			if (index >= length - 1) {
				started = false;
			}
			
			if (geneStreng.get(index).getDirectionString() == Direction.Right) {
				geneStreng.get(index).setScore(1f);
				score++;
			} else {
				geneStreng.get(index).setScore(0f);
				score--;
			}
		}
		

		Vector2 lastPos = new Vector2(this.rigidbody.getPosition());

		
	}

	@Override
	public void update(float dt, int iterations) {
		super.update(dt, iterations);
	}

	public float getScore() {
		return score;
	}

	public ArrayList<Gene> getGeneStreng() {
		return this.geneStreng;
	}
}
