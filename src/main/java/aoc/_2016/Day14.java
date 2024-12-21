package aoc._2016;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.BaseEncoding;

public class Day14 {

	private final String seed;

	private static final Pattern PATTERN1 = Pattern.compile("([0-9a-zA-Z])\\1\\1");

	public Day14(String seed) {
		this.seed = seed;
	}

	public void run(int numKeys) throws NoSuchAlgorithmException {
		List<Integer> indices = new ArrayList<Integer>(numKeys);
		for (int i = 0; indices.size() < numKeys; i++) {
			String base = encode(i);
			Matcher matcher = PATTERN1.matcher(base);
			if (matcher.find()) {
				System.out.println(i + " match1 " + base);
				String repeat = matcher.group(1);
				for (int j = 0; j < 1000; j++) {
					int next = i + j + 1;
					String ext = encode(next);
					if (ext.contains(repeat + repeat + repeat + repeat + repeat)) {
						System.out.println(next + " match2 " + ext + " !!!");
						indices.add(i);
						break;
					}
				}
			}
		}

		for (int i=0; i < indices.size(); i++) {
			System.out.println((i+1) + ": " + indices.get(i));
		}
	}

	static MessageDigest md;
	static Map<Integer, String> allHashes = new HashMap<Integer, String>();

	private String encode(int val) throws NoSuchAlgorithmException {
		String result = allHashes.get(val);
		if (result != null) {
			return result;
		}

		// Part 1
		result = hash(seed + val);

		// Part 2
//		result = seed + val;
//		for (int i = 0; i < 2017; i++) {
//			result = hash(result);
//		}
		allHashes.put(val, result);
		return result;
	}

	private String hash(String toHash) throws NoSuchAlgorithmException {
		if (md == null) md = MessageDigest.getInstance("MD5");
		md.update(toHash.getBytes());
		return BaseEncoding.base16().encode(md.digest()).toLowerCase();
	}

//	private final static char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
//	static String toHex(byte[] bytes) {
//		char[] hexChars = new char[bytes.length * 2];
//		for (int j = 0; j < bytes.length; j++) {
//			int byteValue = bytes[j] & 0xFF;
//			hexChars[j * 2] = HEX_ARRAY[byteValue >>> 4];
//			hexChars[j * 2 + 1] = HEX_ARRAY[byteValue & 0x0F];
//		}
//		return new String(hexChars);
//	}

	public static void main(String[] args) throws Exception {
//		String input = "cuanljph";
//		String input = "abc";
		String input = "ngcjuoqr";
		Day14 worker = new Day14(input);
		worker.run(64);
	}

}
