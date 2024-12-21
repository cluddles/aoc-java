package shared;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5 {

	private static MessageDigest md;
	static {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError(e);
		}
	}

	private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

	public static String toHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int byteValue = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[byteValue >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[byteValue & 0x0F];
		}
		return new String(hexChars);
	}

	public static String hash(String toHash) {
		md.update(toHash.getBytes());
		//return BaseEncoding.base16().encode(md.digest()).toLowerCase();
		return toHex(md.digest());
	}

}
