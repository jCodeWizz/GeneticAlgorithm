package dev.codewizz.utils;

public class Debug {

	public static void error(String message) {
		System.out.println("[ERROR]: " + message);
	}
	
	public static void debug(String message) {
		System.out.println("[DEBUG]: " + message);
	}
	
	public static void info(String message) {
		System.out.println("[INFO]: " + message);
	}
	
	public static void throwError(String message) {
		try {
			error(message);
			throw new Exception(message);
		} catch(Exception e) {		}
	}
	
	public static void throwStateError(String message) {
		try {
			error(message);
			throw new IllegalStateException(message);
		} catch(Exception e) {		}
	}
	
}
