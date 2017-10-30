package de.hska.iwi.bdelab.batchstore;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.util.stream.Stream;

import de.hska.iwi.bdelab.schema.Data;
import de.hska.iwi.bdelab.schema.DataUnit;
import de.hska.iwi.bdelab.schema.PageID;
import de.hska.iwi.bdelab.schema.PageViewEdge;
import de.hska.iwi.bdelab.schema.Pedigree;
import de.hska.iwi.bdelab.schema.UserID;
import manning.tap.DataPailStructure;

import org.apache.hadoop.fs.FileSystem;

import com.backtype.hadoop.pail.Pail;
import com.backtype.hadoop.pail.PailFormatFactory;

public class Batchloader {
	private final String DATA_FILE = "pageviews.txt";
	private Pail<Data>.TypedRecordOutputStream tmpWriteStream;

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
		Data result = new Data();

		StringTokenizer tokenizer = new StringTokenizer(pageview);
		String ip = tokenizer.nextToken();
		String url = tokenizer.nextToken();
		String time = tokenizer.nextToken();

		System.out.println(ip + " " + url + " " + time);

		// ... create Data		                                
		DataUnit dataUnit = new DataUnit();

		UserID userID = new UserID();
		userID.set_user_id(ip);
		
		PageID pageID = new PageID();
		pageID.set_url(url);
		
		PageViewEdge pageView = new PageViewEdge();
		pageView.set_nonce( Long.valueOf(time));
		pageView.set_page(pageID);
		pageView.set_user(userID);
		
		
		dataUnit.set_page_view( pageView );
		
		
		Pedigree pedigree = new Pedigree( Integer.valueOf(time));
		
		result.set_dataunit(dataUnit);
		result.set_pedigree(pedigree);
		

		return result;
	}

	private void writeToPail(Data data) {
		try {
			tmpWriteStream.writeObjects(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	private void importPageviews() {
		boolean LOCAL = false;

		try {
			FileSystem fs = FileUtils.getFs(LOCAL);
			// temporary pail goes to tmp folder
			String newPath = FileUtils.getTmpPath(fs, FileUtils.NEW_PAIL, true, LOCAL);
			// master pail goes to permanent fact store
			String masterPath = FileUtils.getPath(fs, FileUtils.FACT_BASE, FileUtils.MASTER_PAIL, false, LOCAL);


			Pail<Data> tmpPail = Pail.create(fs, newPath, PailFormatFactory.getDefaultCopy().setStructure(new DataPailStructure()));
			Pail<Data> masterPail = Pail.create(fs, masterPath, PailFormatFactory.getDefaultCopy().setStructure(new DataPailStructure()));
			
			tmpWriteStream = tmpPail.openWrite();

			// write facts to new pail
			readPageviewsAsStream();

			tmpWriteStream.close();
			
			// set up master pail and absorb new pail
			masterPail.absorb(tmpPail);
			masterPail.consolidate();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Batchloader loader = new Batchloader();
		loader.importPageviews();
	}
}