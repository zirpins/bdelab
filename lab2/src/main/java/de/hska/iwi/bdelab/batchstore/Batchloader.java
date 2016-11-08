package de.hska.iwi.bdelab.batchstore;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.util.stream.Stream;

import org.apache.hadoop.fs.FileSystem;

import com.backtype.hadoop.pail.Pail;
import com.backtype.hadoop.pail.PailFormatFactory;

import de.hska.iwi.bdelab.schema.Data;
import de.hska.iwi.bdelab.schema.DataUnit;
import de.hska.iwi.bdelab.schema.Page;
import de.hska.iwi.bdelab.schema.PageviewEdge;
import de.hska.iwi.bdelab.schema.Pedigree;
import de.hska.iwi.bdelab.schema.UserID;
import manning.tap.DataPailStructure;

public class Batchloader {
	private final String DATA_FILE = "pageviews.txt";

	private FileSystem fs;

	private String newpath;
	private String masterpath;

	private Pail<Data> newpail;
	private Pail<Data> masterpail;

	private Pail<Data>.TypedRecordOutputStream os;

	public static void main(String[] args) {
		Batchloader loader = new Batchloader();
		loader.importPageviews();
	}

	@SuppressWarnings("unchecked")
	private void importPageviews() {
		try {

			// set up filesystem
			fs = FileUtils.getFs();
			newpath = FileUtils.getTmpPath(fs, FileUtils.NEW_PAIL, true);
			masterpath = FileUtils.getTmpPath(fs, FileUtils.MASTER_PAIL, false);

			// set up pails
			newpail = Pail.create(fs, newpath,
					PailFormatFactory.getDefaultCopy().setStructure(new DataPailStructure()));
			masterpail = Pail.create(fs, masterpath,
					PailFormatFactory.getDefaultCopy().setStructure(new DataPailStructure()));

			// write facts to new pail
			os = newpail.openWrite();
			readPageviewsAsStream();
			os.close();

			// absorb new pail into master
			masterpail.absorb(newpail);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readPageviewsAsStream() {
		try {
			URI uri = Batchloader.class.getClassLoader().getResource(DATA_FILE).toURI();
			try (Stream<String> stream = Files.lines(Paths.get(uri))) {
				stream.forEach(element -> {
					try {
						os.writeObject(getDatafromString(element));
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private Data getDatafromString(String pageview) {
		StringTokenizer tokenizer = new StringTokenizer(pageview);
		String ip = tokenizer.nextToken();
		String url = tokenizer.nextToken();
		String time = tokenizer.nextToken();

		System.out.println(ip + " " + url + " " + time);

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