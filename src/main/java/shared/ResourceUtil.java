package shared;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dan Fielding
 */
public class ResourceUtil {

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
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try (InputStream resource = classLoader.getResourceAsStream(name)) {
			return new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8))
							.lines()
							.collect(Collectors.toList());
		}
	}

	public static String readString(String name) throws IOException {
		return readAllLines(name).stream().collect(Collectors.joining("\n"));
	}

}
