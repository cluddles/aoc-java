package aoc._2015;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day12 {

	// p1
	Pattern numberPattern = Pattern.compile("(-*\\d+)");

	public void solvePath(String path) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(path));

		int result = 0;
		for (String line : lines) {
			result += eval(line);
		}
		System.out.println(result);
	}

	public int score(String fragment) {
		int score = 0;
		Matcher matcher = numberPattern.matcher(fragment);
		while (matcher.find()) {
			score += Integer.parseInt(matcher.group(1));
		}
		return score;
	}

	public int eval(String fragment) {
		// Strip out all braced content, remember for later
		List<String> braceList = new ArrayList<>();
		String current = fragment;
		while (true) {
			int open = -1, close = -1, level = -1;
			for (int i = 0; i < current.length(); i++) {
				if (current.charAt(i) == '{') {
					level++;
					if (level == 0) { open = i; }
				} else if (current.charAt(i) == '}') {
					if (level == 0) { close = i; break; }
					level--;
				}
			}
			if (open == -1 || close == -1) break;

			String brace = current.substring(open + 1, close);
			braceList.add(brace);
			current = current.substring(0, open) + current.substring(close+1);
		}
		// Now discard the whole lot if we have :"red" at the top-level
		if (current.contains(":\"red\"")) return 0;
		// Otherwise, score fragment and evaluate the braced content
		int result = score(current);
		for (String braced : braceList) {
			result += eval(braced);
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
		Day12 worker = new Day12();
		//System.out.println(worker.eval("[1,2,3]"));
		// System.out.println(worker.eval("[1,{\"c\":\"red\",\"b\":2},3]"));
		//System.out.println(worker.eval("{\"d\":\"red\",\"e\":[1,2,3,4],\"f\":5}"));
		//System.out.println(worker.eval("[1,\"red\",5]"));

		worker.solvePath("/home/danielf/Documents/extra/adventOfCode/2015/day12.input");
	}

}
