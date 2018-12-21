package aoc._2017;

import java.util.ArrayList;
import java.util.List;

import shared.ResourceUtil;
import shared.Test;

/**
 * @author Dan Fielding
 */
public class Day9 {

	/*
		--- Day 9: Stream Processing ---

		A large stream blocks your path. According to the locals, it's not safe
		to cross the stream at the moment because it's full of garbage. You look
		down at the stream; rather than water, you discover that it's a stream
		of characters.

		You sit for a while and record part of the stream (your puzzle input).
		The characters represent groups - sequences that begin with { and end
		with }. Within a group, there are zero or more other things, separated
		by commas: either another group or garbage. Since groups can contain
		other groups, a } only closes the most-recently-opened unclosed group -
		that is, they are nestable. Your puzzle input represents a single, large
		group which itself contains many smaller ones.

		Sometimes, instead of a group, you will find garbage. Garbage begins
		with < and ends with >. Between those angle brackets, almost any
		character can appear, including { and }. Within garbage, < has no
		special meaning.

		In a futile attempt to clean up the garbage, some program has canceled
		some of the characters within it using !: inside garbage, any character
		that comes after ! should be ignored, including <, >, and even another
		!.

		You don't see any characters that deviate from these rules. Outside
		garbage, you only find well-formed groups, and garbage always terminates
		according to the rules above.

		Here are some self-contained pieces of garbage:

		- <>, empty garbage.
		- <random characters>, garbage containing random characters.
		- <<<<>, because the extra < are ignored.
		- <{!>}>, because the first > is canceled.
		- <!!>, because the second ! is canceled, allowing the > to terminate
		  the garbage.
		- <!!!>>, because the second ! and the first > are canceled.
		- <{o"i!a,<{i<a>, which ends at the first >.

		Here are some examples of whole streams and the number of groups they
		contain:

		- {}, 1 group.
		- {{{}}}, 3 groups.
		- {{},{}}, also 3 groups.
		- {{{},{},{{}}}}, 6 groups.
		- {<{},{},{{}}>}, 1 group (which itself contains garbage).
		- {<a>,<a>,<a>,<a>}, 1 group.
		- {{<a>},{<a>},{<a>},{<a>}}, 5 groups.
		- {{<!>},{<!>},{<!>},{<a>}}, 2 groups (since all but the last > are
		  canceled).

		Your goal is to find the total score for all groups in your input.
		Each group is assigned a score which is one more than the score of the
		group that immediately contains it. (The outermost group gets a score
		of 1.)

		- {}, score of 1.
		- {{{}}}, score of 1 + 2 + 3 = 6.
		- {{},{}}, score of 1 + 2 + 2 = 5.
		- {{{},{},{{}}}}, score of 1 + 2 + 3 + 3 + 3 + 4 = 16.
		- {<a>,<a>,<a>,<a>}, score of 1.
		- {{<ab>},{<ab>},{<ab>},{<ab>}}, score of 1 + 2 + 2 + 2 + 2 = 9.
		- {{<!!>},{<!!>},{<!!>},{<!!>}}, score of 1 + 2 + 2 + 2 + 2 = 9.
		- {{<a!>},{<a!>},{<a!>},{<ab>}}, score of 1 + 2 = 3.

		What is the total score for all groups in your input?


		--- Part Two ---

		Now, you're ready to remove the garbage.

		To prove you've removed it, you need to count all of the characters
		within the garbage. The leading and trailing < and > don't count, nor do
		any canceled characters or the ! doing the canceling.

		- <>, 0 characters.
		- <random characters>, 17 characters.
		- <<<<>, 3 characters.
		- <{!>}>, 2 characters.
		- <!!>, 0 characters.
		- <!!!>>, 0 characters.
		- <{o"i!a,<{i<a>, 10 characters.

		How many non-canceled characters are within the garbage in your puzzle
		input?
	*/

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
