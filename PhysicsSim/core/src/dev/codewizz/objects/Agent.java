package dev.codewizz.objects;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

import dev.codewizz.objects.ai.Gene;
import dev.codewizz.utils.Utils;

public class Agent extends Box {

	private static float movementSpeed = 5f;
	public static boolean started = true;
	public static int length = 500;
	
	public static int mode = 2;

	private ArrayList<Gene> geneStreng = new ArrayList<Gene>();
	private int index = 0;
	private float score = 0.5f;
	private Vector2 origin;
	private Vector2 prevPos, nextPos;

	public Agent(float x, float y, float s) {
		super(x, y, s, s, Utils.getRandomColor());
		this.id = ID.Agent;
		init();
		origin = new Vector2(this.getRigidbody().getPosition());
		nextPos = new Vector2(this.getRigidbody().getPosition());
		prevPos = new Vector2(this.getRigidbody().getPosition());
	}

	public Agent(float x, float y, float s, ArrayList<Gene> genes) {
		super(x, y, s, s, Utils.getRandomColor());
		this.id = ID.Agent;
		this.geneStreng = genes;
		origin = new Vector2(this.getRigidbody().getPosition());
		nextPos = new Vector2(this.getRigidbody().getPosition());
		prevPos = new Vector2(this.getRigidbody().getPosition());
	}

	private void init() {
		for (int i = 0; i < length; i++) {
			geneStreng.add(new Gene(i));
		}
	}

	public void reset() {
		index = 0;
		this.getRigidbody().moveTo(origin);
	}

	public void tick() {
		
		if (started) {
			
			prevPos = new Vector2(nextPos);
			
			
			Vector2 v = new Vector2(geneStreng.get(index).getDir()).scl(movementSpeed);
			this.rigidbody.move(v);
			nextPos = new Vector2(this.rigidbody.getPosition());
			
			System.out.println();
			System.out.println("Prev: " + prevPos);
			System.out.println("Delta: " + v);
			System.out.println("Next: " + nextPos);
			System.out.println("Gene: " + geneStreng.get(index).getDirectionString());
			System.out.println("B: " + condition(prevPos, nextPos));
			
			if(condition(prevPos, nextPos)) {
				geneStreng.get(index).setScore(1f);
				score++;
			} else {
				geneStreng.get(index).setScore(0f);
				score--;
			}
			
			index++;

			if (index >= length - 1) {
				started = false;
			}
		}
	}
	
	private boolean condition(Vector2 prev, Vector2 next) {
		
		if(mode == 0) {
			// go right
			return next.x > prev.x;		
		} else if(mode == 1) {
			// go left
			return next.x < prev.x;
		} else if(mode == 2) {
			// stand in middle
			float d1 = prev.dst2(new Vector2(0, prev.y));
			float d2 = next.dst2(new Vector2(0, next.y));
			return d2 < d1;
		}
		
		return false;
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
