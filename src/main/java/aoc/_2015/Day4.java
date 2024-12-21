package aoc._2015;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.common.io.BaseEncoding;

public class Day4 {

	public void run(String input) throws NoSuchAlgorithmException {
		for (int i = 0; ; i++) {
			String md5 = md5(input + i);
			if (md5.startsWith("000000")) {
				System.out.println(i);
				break;
			}
		}
	}

	String md5(String text) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(text.getBytes());
		return BaseEncoding.base16().encode(md.digest()).toLowerCase();
	}

	public static void main(String[] args) throws Exception {
		String input = "yzbqklnj";
		Day4 worker = new Day4();
		worker.run(input);
	}

}
