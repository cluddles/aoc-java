package aoc._2017;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import shared.ResourceUtil;
import shared.Test;

public class Day7 {

	class Node {
		private final String name;
		private final int weight;
		private final List<String> childNames;
		// We'll populate these later
		private List<Node> children = new ArrayList<>();
		private Node parent;

		Node(String name, int weight, List<String> childNames) {
			this.name = name;
			this.weight = weight;
			this.childNames = childNames;
		}

		int totalWeight() {
			return weight + children.stream().mapToInt(Node::totalWeight).sum();
		}

		Node root() {
			return parent == null? this : parent.root();
		}

		Unbalanced findUnbalanced() {
			return findUnbalanced(totalWeight());
		}

		// assuming there is one broken node in this tree
		Unbalanced findUnbalanced(int expectedTotalWeight) {
			Node mismatch = null;
			int firstChildWeight = -1;
			int expectedChildWeight = -1;
			for (Node child : children) {
				int childTotalWeight = child.totalWeight();
				// System.out.println(this.name + " -> " + child.name + ": " + child.totalWeight());
				if (firstChildWeight == -1) {
					firstChildWeight = childTotalWeight;
				} else {
					if (childTotalWeight != firstChildWeight) {
						// One of the children is the wrong weight
						// Either this one or the first one
						if (mismatch == null) {
							// Only one deviation so far, so assume it's the
							// current child that's wrong
							mismatch = child;
							expectedChildWeight = firstChildWeight;
						} else {
							// Multiple, so it must be the first child
							mismatch = children.iterator().next();
							expectedChildWeight = childTotalWeight;
							break;
						}
					}
				}
			}
			// Call the mismatched node recursively
			if (mismatch != null) {
				return mismatch.findUnbalanced(expectedChildWeight);
			}
			// Our own weight must be wrong
			return new Unbalanced(name, (expectedTotalWeight - totalWeight() + weight));
		}
	}

	class Unbalanced {
		private final String name;
		private final int newWeight;

		Unbalanced(String name, int newWeight) {
			this.name = name;
			this.newWeight = newWeight;
		}
	}

	public Node processNodes(List<String> lines) {
		Map<String, Node> nodes = new HashMap<>();
		// All nodes have the "xxxx (yy)" bit
		// Only parents have the "-> aa, bb, cc, dd" stuff
		Pattern nodePattern = Pattern.compile("^(\\w*) \\((\\d*)\\)");
		Pattern childrenPattern = Pattern.compile(" -> (.*)$");
		for (String line : lines) {
			Matcher nodeMatcher = nodePattern.matcher(line);
			if (nodeMatcher.find()) {
				Matcher childrenMatcher = childrenPattern.matcher(line);
				String childNames = null;
				if (childrenMatcher.find()) {
					childNames = childrenMatcher.group(1);
				}

				String name = nodeMatcher.group(1);
				nodes.put(
						name,
						new Node(name,
								Integer.valueOf(nodeMatcher.group(2)),
								childNames == null?
										ImmutableList.of() :
										Arrays.stream(childNames.split(","))
												.map(String::trim)
												.collect(Collectors.toList())));
			}
		}

		// Now populate all the children, parent crap
		for (Node node : nodes.values()) {
			for (String childName : node.childNames) {
				Node child = nodes.get(childName);
				node.children.add(child);
				child.parent = node;
			}
		}

		// Root node
		return nodes.values().iterator().next().root();
	}

	public void examples() {
		List<String> lines = ImmutableList.of(
				"pbga (66)",
				"xhth (57)",
				"ebii (61)",
				"havc (66)",
				"ktlj (57)",
				"fwft (72) -> ktlj, cntj, xhth",
				"qoyq (66)",
				"padx (45) -> pbga, havc, qoyq",
				"tknk (41) -> ugml, padx, fwft",
				"jptl (61)",
				"ugml (68) -> gyxo, ebii, jptl",
				"gyxo (61)",
				"cntj (57)");
		Node root = processNodes(lines);
		Test.check(root.name, "tknk");

		Unbalanced unbalanced = root.findUnbalanced();
		Test.check(unbalanced.name, "ugml");
		Test.check(unbalanced.newWeight, 60);
	}

	public static void main(String[] args) throws Exception {
		Day7 day = new Day7();
		day.examples();

		List<String> lines = ResourceUtil.readAllLines("2017/day7.input");

		Node root = day.processNodes(lines);
		System.out.println(root.name);

		Unbalanced unbalanced = root.findUnbalanced();
		System.out.println(unbalanced.name + ", " + unbalanced.newWeight);
	}

}
