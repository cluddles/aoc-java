package aoc._2015;

import java.io.IOException;
import java.util.List;

import shared.ResourceUtil;

public class Day2 {

	class Present {
		final int[] dim;

		Present(int[] dim) {
			this.dim = dim;
			sort();
		}

		void sort() {
			comp(0, 1);
			comp(1, 2);
			comp(0, 1);
		}

		void comp(int a, int b) {
			if (dim[a]> dim[b]) {
				int temp = dim[a];
				dim[a] = dim[b];
				dim[b] = temp;
			}
		}

		int surfaceArea() {
			// Total circumference
			return 2 * dim[0] * dim[1]
					+ 2 * dim[1] * dim[2]
					+ 2 * dim[0] * dim[2];
		}
	
		int slack() {
			// Smallest side
			return dim[0] * dim[1];
		}
	
		int ribbon() {
			// Ribbon sides + bow
			return dim[0]*2 + dim[1]*2
					+ (dim[0] * dim[1] * dim[2]);
		}
	}

	public void run() throws IOException {
		List<String> lines = ResourceUtil.readAllLines("/2015/day2.input");
		int paper = 0;
		int ribbon = 0;
		for (String line : lines) {
			String[] split = line.split("x");
			Present present = new Present(new int[]{
					Integer.parseInt(split[0]),
					Integer.parseInt(split[1]),
					Integer.parseInt(split[2])
			});
			paper += (present.surfaceArea() + present.slack());
			ribbon += present.ribbon();
		}
		System.out.println(paper);
		System.out.println(ribbon);
	}

	public static void main(String[] args) throws Exception {
		Day2 worker = new Day2();
		worker.run();
	}

}
