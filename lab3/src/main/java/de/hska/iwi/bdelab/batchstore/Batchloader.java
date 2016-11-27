package de.hska.iwi.bdelab.batchstore;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.util.stream.Stream;

import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;

import com.backtype.hadoop.pail.Pail;

import de.hska.iwi.bdelab.schema.Data;
import de.hska.iwi.bdelab.schema.DataUnit;
import de.hska.iwi.bdelab.schema.Page;
import de.hska.iwi.bdelab.schema.PageviewEdge;
import de.hska.iwi.bdelab.schema.Pedigree;
import de.hska.iwi.bdelab.schema.UserID;

public class Batchloader {
	private static Logger log = org.slf4j.LoggerFactory.getLogger(Batchloader.class);

	private final String DATA_FILE = "pageviews2.txt";

	private FileSystem fs;

	private Pail<Data> newPail;
	private Pail<Data> masterPail;

	private Pail<Data>.TypedRecordOutputStream os;

	public static void main(String[] args) throws Exception {
		boolean reset = false;
		boolean master = false;

		for (String arg : args) {
			switch (arg) {
			case "-r":
				reset = true;
				break;
			case "-m":
				master = true;
				break;
			default:
				printUsage();
				break;
			}
		}
		new Batchloader().importPageviews(reset, master);
	}

	private static void printUsage() {
		System.out.println("Usage: Batchloader <-r> <-m>");
		System.out.println("-m append to master pail");
		System.out.println("-r reset batch data files");
		System.exit(1);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void importPageviews(boolean reset, boolean master) throws Exception {
		// set up filesystem
		fs = FileUtils.getFs();

		// optionally reset all data
		if (reset) {
			FileUtils.resetBatchFiles();
		}

		newPail = new Pail(fs, FileUtils.NEW_ROOT);

		// write facts to new pail
		os = newPail.openWrite();
		readPageviewsAsStream();
		os.close();

		// optionally move newData to master while preserving the newPail to
		// receive incoming data
		if (master) {
			masterPail = new Pail(fs, FileUtils.MASTER_ROOT);
			masterPail.absorb(newPail);
		}
	}

	private void readPageviewsAsStream() throws Exception {
		URI uri = Batchloader.class.getClassLoader().getResource(DATA_FILE).toURI();
		Stream<String> stream = Files.lines(Paths.get(uri));
		stream.forEach(element -> {
			try {
				os.writeObject(getDatafromString(element));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		stream.close();
	}

	private Data getDatafromString(String pageview) {
		StringTokenizer tokenizer = new StringTokenizer(pageview);
		String ip = tokenizer.nextToken();
		String url = tokenizer.nextToken();
		String time = tokenizer.nextToken();

		log.info(ip + " " + url + " " + time);

		UserID uid1 = new UserID();
		uid1.set_ip(ip);

		Page pg1 = new Page();
		pg1.set_url(url);

		PageviewEdge pve1 = new PageviewEdge();
		pve1.set_user(uid1);
		pve1.set_page(pg1);
		pve1.set_time(Integer.parseInt(time));

		DataUnit du1 = new DataUnit();
		du1.set_pageview(pve1);

		Pedigree pd1 = new Pedigree();
		pd1.set_true_as_of_secs((int) (System.currentTimeMillis() / 1000));
		Data data = new Data();
		data.set_dataunit(du1);
		data.set_pedigree(pd1);

		return data;
	}
}
