package shared;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceUtil {

	private static URL find(String path) {
		URL result = findResource(path);
		if (result == null) {
			result = findFile(path);
		}
		if (result == null) {
			throw new IllegalArgumentException("Resource not found: \"" + path + "\"");
		}
		return result;
	}

	private static URL findResource(String path) {
		return ResourceUtil.class.getResource("/" + path);
	}

	private static URL findFile(String path) {
		File file = new File("aoc-secret/" + path);
		if (file.exists()) {
			try {
				return file.toURI().toURL();
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			return null;
		}
	}

	/**
	 * Loads text file lines from src/main/resources-based location.
	 *
	 * @param name
	 * 		Filename.
	 * @return Lines as a list.
	 * @throws IOException
	 * 		If read failed.
	 */
	public static List<String> readAllLines(String name) throws IOException {
		URL url = find(name);
		return new BufferedReader(new InputStreamReader(url.openStream()))
				.lines()
				.collect(Collectors.toList());
	}

	public static String readString(String name) throws IOException {
		return String.join("\n", readAllLines(name));
	}

}
