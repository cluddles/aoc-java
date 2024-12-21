package aoc._2016;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.common.io.BaseEncoding;

public class Day5 {

	private void determinePassword(String input)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest hasher = MessageDigest.getInstance("MD5");

		int index = 0;
		StringBuilder passwordBuilder = new StringBuilder();
		while (passwordBuilder.length() < 8) {
//			System.out.println(index + ": " + passwordBuilder.length());
			String message = input + index;
			byte[] md5Bytes = hasher.digest(message.getBytes("UTF-8"));
			String md5 = BaseEncoding.base16().encode(md5Bytes);
//			System.out.println(md5);
			if (md5.startsWith("00000")) {
				passwordBuilder.append(md5.charAt(5));
			}
			index++;
		}

		System.out.println(passwordBuilder.toString());
	}

	private void determinePasswordComplex(String input)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest hasher = MessageDigest.getInstance("MD5");

		int index = 0;
		int set = 0;
		Character[] passwordBuilder = new Character[8];
		while (set < 8) {
			String message = input + index;
			byte[] md5Bytes = hasher.digest(message.getBytes("UTF-8"));
			String md5 = BaseEncoding.base16().encode(md5Bytes);
			if (md5.startsWith("00000")) {
				if (md5.charAt(5) >= '0' && md5.charAt(5) <= '7') {
					int insertIndex = Integer.parseInt(String.valueOf(md5.charAt(5)));
					char insertChar = md5.charAt(6);
					if (passwordBuilder[insertIndex] == null) {
						set++;
						passwordBuilder[insertIndex] = insertChar;
					}
				}
			}
			index++;
		}

		for (Character c : passwordBuilder) {
			System.out.print(c);
		}
		System.out.println();
	}

	public static void main(String[] args) throws Exception {
		String input = "ffykfhsq";

		Day5 worker = new Day5();
		worker.determinePasswordComplex(input);
	}

}
