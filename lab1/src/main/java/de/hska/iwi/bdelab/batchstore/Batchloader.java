package de.hska.iwi.bdelab.batchstore;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.util.stream.Stream;

import de.hska.iwi.bdelab.schema.Data;
import org.apache.hadoop.fs.FileSystem;

public class Batchloader {
	private final String DATA_FILE = "pageviews.txt";

	// ...

	private void readPageviewsAsStream() {
		try {
			URI uri = Batchloader.class.getClassLoader().getResource(DATA_FILE).toURI();
			try (Stream<String> stream = Files.lines(Paths.get(uri))) {
				stream.forEach(line -> writeToPail(getDatafromString(line)));
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
		boolean LOCAL = false;

		try {
			FileSystem fs = FileUtils.getFs(LOCAL);
			// temporary pail goes to tmp folder
			String newPath = FileUtils.getTmpPath(fs, FileUtils.NEW_PAIL, true, LOCAL);
			// master pail goes to permanent fact store
			String masterPath = FileUtils.getPath(fs, FileUtils.FACT_BASE, FileUtils.MASTER_PAIL, false, LOCAL);

			// set up new pail and a stream
			// ...

			// write facts to new pail
			readPageviewsAsStream();

			// set up master pail and absorb new pail
			// ...

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Batchloader loader = new Batchloader();
		loader.importPageviews();
	}
}