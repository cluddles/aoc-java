package aoc._2015;

import java.util.regex.Pattern;

public class Day11 {

	static Pattern doublePattern = Pattern.compile("(\\w)\\1.*(\\w)\\2");
	static Pattern invalidCharPattern = Pattern.compile("[iol]");

	public static String nextPassword(String base) {
		String current = base;
		while (!isValid(current)) {
			current = increment(current);
		}
		return current;
	}

	public static String increment(String password) {
		char[] chars = password.toCharArray();
		int carry = 1;
		int pos = password.length()-1;
		while (carry == 1) {
			chars[pos] = (char) (chars[pos] + 1);
			if (chars[pos] > 'z') chars[pos] = 'a';
			else carry = 0;
			pos--;
		}
		// Quick-skip invalid chars
		boolean clear = false;
		for (int i = 0; i < chars.length; i++) {
			if (clear) {
				chars[i] = 'a';
			} else {
				clear = true;
				if (chars[i] == 'i') chars[i] = 'j';
				else if (chars[i] == 'l') chars[i] = 'm';
				else if (chars[i] == 'o') chars[i] = 'p';
				else clear = false;
			}
		}
		return new String(chars);
	}

	public static boolean isValid(String password) {
		if (!doublePattern.matcher(password).find()) return false;
		if (invalidCharPattern.matcher(password).find()) return false;
		for (int i = 0; i < password.length()-2; i++) {
			char c1 = password.charAt(i);
			char c2 = password.charAt(i+1);
			char c3 = password.charAt(i+2);
			if (c2 == c1+1 && c3 == c2+1) return true;
		}
		return false;
	}

	public static void main(String[] args) throws Exception {
//		System.out.println(isValid("hijklmmn"));
//		System.out.println(isValid("abbceffg"));
//		System.out.println(isValid("abbcegjk"));
//		System.out.println(isValid("abcdffaa"));
//		System.out.println(isValid("ghjaabcc"));
//
//		System.out.println(nextPassword("abcdefgh"));
//		System.out.println(nextPassword("ghijklmn"));

		// p1
		System.out.println(nextPassword("hxbxwxba"));

		// p2
		System.out.println(nextPassword(increment("hxbxxyzz")));
	}

}
