package aoc._2016;

public class Day16 {

	public void run(String input, int length) {
		String encoded = encode(input, length);
		System.out.println("Encoded: " + encoded);
		encoded = encoded.substring(0, length);
		System.out.println("Trimmed: " + encoded);
		String checksum = checksum(encoded);
		System.out.println("Checksum: " + checksum);
	}

	private String encode(String text, int length) {
		while (text.length() < length) {
			StringBuilder sb = new StringBuilder(text.length() * 2 + 1);
			sb.append(text).append(0);
			for (int i = text.length()-1; i >= 0; i--) {
				switch (text.charAt(i)) {
				case '0':
					sb.append('1'); break;
				case '1':
					sb.append('0'); break;
				default:
					sb.append(text.charAt(i)); break;
				}
			}
			text = sb.toString();
		}
		return text;
	}

	private String checksum(String text) {
		while ((text.length() % 2) == 0) {
			StringBuilder sb = new StringBuilder(text.length() / 2);
			for (int i = 0; i < text.length(); i+=2) {
				sb.append((text.charAt(i) == text.charAt(i + 1))? '1' : '0');
			}
			text = sb.toString();
		}
		return text;
	}

	public static void main(String[] args) throws Exception {
		Day16 worker = new Day16();
		// worker.run("10000", 20);
		//worker.run("01111010110010011", 272);
		worker.run("01111010110010011", 35651584);
	}

}
