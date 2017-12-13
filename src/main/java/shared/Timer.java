package shared;

/**
 * @author Dan Fielding
 */
public class Timer {

	private static long startTime;

	public static void start() {
		startTime = System.currentTimeMillis();
	}

	public static String end() {
		return "Done: " + (System.currentTimeMillis() - startTime) + "ms";
	}

}
