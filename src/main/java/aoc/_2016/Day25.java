package aoc._2016;

public class Day25 {

	public void evalPart1() throws InterruptedException {
		int a = 196;
		int b = 0;
		int d = a + 2534;
		while (true) {
			a = d;
			while (a != 0) {
				b = a % 2;
				a = a / 2;
				Thread.sleep(50);
				System.out.println(b);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new Day25().evalPart1();
	}

}
