package dev.codewizz.utils;

public class StopWatch {

	private long start, end, time;

	
	public void start() {
		start = System.currentTimeMillis();
	}
	
	public void end() {
		end = System.currentTimeMillis();
		time = end - start;
	}
	
	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

	public long getTime() {
		return time;
	}
}
