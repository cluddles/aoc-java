package aoc._2017;

import java.util.ArrayList;
import java.util.List;

import shared.ResourceUtil;
import shared.Test;

public class Day9 {

	class Buffer {
		final char[] data;
		int pos;

		Buffer(String data) {
			this.data = data.toCharArray();
		}
		char peek() { return data[pos];  }
		char pop() { return data[pos++]; }
	}

	class Group {
		List<Group> groups = new ArrayList<>();
		int garbage;

		int groupScore(int level) {
			return level + groups.stream()
					.mapToInt(group -> group.groupScore(level + 1))
					.sum();
		}
		int garbageScore() {
			return garbage + groups.stream()
					.mapToInt(Group::garbageScore)
					.sum();
		}
	}

	public int readGarbage(Buffer buffer) {
		int removed = 0;
		// Starts with <
		Test.check(buffer.pop(), '<');
		while (true) {
			switch (buffer.pop()) {
			case '>':
				// Done
				return removed;
			case '!':
				// Consume the next character and ignore it
				buffer.pop(); break;
			default:
				removed++;
				break;
			}
		}
	}

	public Group readGroup(Buffer buffer) {
		Group result = new Group();
		// Starts with {
		Test.check(buffer.pop(), '{');
		while (true) {
			switch (buffer.peek()) {
			case '{':
				// Another group
				result.groups.add(readGroup(buffer));
				break;
			case '<':
				// Some garbage
				result.garbage += readGarbage(buffer);
				break;
			case ',':
				// Comma separator
				buffer.pop();
				break;
			case '}':
				// Ends with }
				buffer.pop();
				return result;
			default:
				throw new RuntimeException("Naughty character");
			}
		}
	}

	public int groupScore(String input) {
		return readGroup(new Buffer(input)).groupScore(1);
	}
	public int garbage(String input) {
		// Make these into proper groups so we can parse them...
		return readGroup(new Buffer("{" + input + "}")).garbageScore();
	}

	public void examples() {
		/*
		- {}, score of 1.
		- {{{}}}, score of 1 + 2 + 3 = 6.
		- {{},{}}, score of 1 + 2 + 2 = 5.
		- {{{},{},{{}}}}, score of 1 + 2 + 3 + 3 + 3 + 4 = 16.
		- {<a>,<a>,<a>,<a>}, score of 1.
		- {{<ab>},{<ab>},{<ab>},{<ab>}}, score of 1 + 2 + 2 + 2 + 2 = 9.
		- {{<!!>},{<!!>},{<!!>},{<!!>}}, score of 1 + 2 + 2 + 2 + 2 = 9.
		- {{<a!>},{<a!>},{<a!>},{<ab>}}, score of 1 + 2 = 3.
		*/
		Test.check(groupScore("{}"), 1);
		Test.check(groupScore("{{{}}}"), 6);
		Test.check(groupScore("{{},{}}"), 5);
		Test.check(groupScore("{{{},{},{{}}}}"), 16);
		Test.check(groupScore("{<a>,<a>,<a>,<a>}"), 1);
		Test.check(groupScore("{{<ab>},{<ab>},{<ab>},{<ab>}}"), 9);
		Test.check(groupScore("{{<!!>},{<!!>},{<!!>},{<!!>}}"), 9);
		Test.check(groupScore("{{<a!>},{<a!>},{<a!>},{<ab>}}"), 3);

		/*
		- <>, 0 characters.
		- <random characters>, 17 characters.
		- <<<<>, 3 characters.
		- <{!>}>, 2 characters.
		- <!!>, 0 characters.
		- <!!!>>, 0 characters.
		- <{o"i!a,<{i<a>, 10 characters.
		 */
		Test.check(garbage("<>"), 0);
		Test.check(garbage("<random characters>"), 17);
		Test.check(garbage("<<<<>"), 3);
		Test.check(garbage("<{!>}>"), 2);
		Test.check(garbage("<!!>"), 0);
		Test.check(garbage("<!!!>>"), 0);
		Test.check(garbage("<{o\"i!a,<{i<a>"), 10);
	}

	public void main(String input) {
		Buffer buffer = new Buffer(input);
		Group group = readGroup(buffer);
		System.out.println(group.groupScore(0));
		System.out.println(group.garbageScore());
	}

	public static void main(String[] args) throws Exception {
		Day9 day = new Day9();
		day.examples();

		String input = ResourceUtil.readString("2017/day9.input");
		day.main(input);
	}
}
