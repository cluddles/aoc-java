package aoc._2015;

public class Day10 {

	public void solve(String input, int iterations) {
		String current = input;
		for (int i = 0; i < iterations; i++) {
			StringBuilder result = new StringBuilder();
			int pos = 0;
			while (pos < current.length()) {
				char c = current.charAt(pos);
				int count = 1;
				for (int j = pos+1; j < current.length(); j++) {
					if (current.charAt(j) != c) break;
					count++;
				}
				result.append(count).append(c);
				pos += count;
			}
			current = result.toString();
			//System.out.println(current);
			System.out.println(current.length());
		}
	}

	public static void main(String[] args) throws Exception {
		Day10 worker = new Day10();
		worker.solve("1321131112", 50);
	}

}
