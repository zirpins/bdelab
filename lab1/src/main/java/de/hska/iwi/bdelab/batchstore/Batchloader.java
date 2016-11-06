package de.hska.iwi.bdelab.batchstore;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.util.stream.Stream;

import de.hska.iwi.bdelab.schema.Data;

public class Batchloader {

	private final String DATA_FILE = "pageviews.txt";
	
	// ...

	private void readPageviewsAsStream() {
		try {
			URI uri = Batchloader.class.getClassLoader().getResource(DATA_FILE).toURI();
			try (Stream<String> stream = Files.lines(Paths.get(uri))) {
				stream.forEach(element -> writeToPail(getDatafromString(element)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
	}

	private Data getDatafromString(String pageview) {
		Data result = null;

		StringTokenizer tokenizer = new StringTokenizer(pageview);
		String ip = tokenizer.nextToken();
		String url = tokenizer.nextToken();
		String time = tokenizer.nextToken();

		System.out.println(ip + " " + url + " " + time);

		// ... create Data

		return result;
	}

	private void writeToPail(Data data) {
		// ...
	}
	
	private void importPageviews() {		
		// ...
		readPageviewsAsStream();
		// ...
	}

	public static void main(String[] args) {
		Batchloader loader = new Batchloader();
		loader.importPageviews();
	}
}