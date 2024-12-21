package shared;

public class Timer {

	private static long startTime;

	public static void start() {
		startTime = System.currentTimeMillis();
	}

	public static long end() {
		return (System.currentTimeMillis() - startTime);
	}

	public static void startMessage() {
		System.out.println("----------");
		start();
	}

	public static void endMessage() {
		System.out.println("-- Done in " + end() + "ms");
	}

}
