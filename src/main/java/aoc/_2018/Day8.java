package aoc._2018;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import shared.ResourceUtil;
import shared.Test;

public class Day8 {

	static class Node {
		List<Node>    children = new ArrayList<>();
		List<Integer> meta     = new ArrayList<>();

		static Node parse(StringTokenizer tokenizer) {
			Node result     = new Node();
			int numChildren = next(tokenizer);
			int numMeta     = next(tokenizer);
			for (int i = 0; i < numChildren; i++) {
				result.children.add(parse(tokenizer));
			}
			for (int i = 0; i < numMeta; i++) {
				result.meta.add(next(tokenizer));
			}
			return result;
		}

		static int next(StringTokenizer tokenizer) {
			return Integer.parseInt(tokenizer.nextToken());
		}

		int metaSum() {
			// Part one
			return  meta.stream()
					.mapToInt(Integer::valueOf)
					.sum() +
					children.stream()
					.mapToInt(Node::metaSum)
					.sum();
		}

		int value() {
			// No children - sum of meta
			if (children.isEmpty()) return metaSum();
			// Children - some mumbo jumbo
			int sum = 0;
			for (int index : meta) {
				if (index > 0 && index <= children.size()) {
					sum += children.get(index - 1).value();
				}
			}
			return sum;
		}
	}

	private Node parseInput(String input) {
		StringTokenizer tokenizer = new StringTokenizer(input, " ");
		return Node.parse(tokenizer);
	}

	public int evalPart1(String input) {
		Node root = parseInput(input);
		return root.metaSum();
	}

	public int evalPart2(String input) {
		Node root = parseInput(input);
		return root.value();
	}

	public static void main(String[] args) throws Exception {
		Day8   solver  = new Day8();
		String input   = ResourceUtil.readString("2018/day8.input");
		String example = "2 3 0 3 10 11 12 1 1 0 1 99 2 1 1 2";

		// Part 1
		// Example
		Test.check(solver.evalPart1(example), 138);
		// Vs Input
		System.out.println(solver.evalPart1(input));

		// Part 2
		// Example
		Test.check(solver.evalPart2(example), 66);
		// Vs Input
		System.out.println(solver.evalPart2(input));
	}

}
