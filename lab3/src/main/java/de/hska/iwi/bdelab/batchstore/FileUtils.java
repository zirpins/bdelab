package de.hska.iwi.bdelab.batchstore;

import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.backtype.hadoop.pail.Pail;
import com.backtype.hadoop.pail.PailFormatFactory;

import manning.tap.DataPailStructure;

public class FileUtils {
	public static final String CORE_SITE = "/usr/local/lib/hadoop-2.7.0/etc/hadoop/core-site.xml";
	public static final String HDFS_SITE = "/usr/local/lib/hadoop-2.7.0/etc/hadoop/hdfs-site.xml";

	public static final String HDFS_BASE = "/user/hadoop";
	public static final String ROOT = HDFS_BASE + "/tmp/bderoot/";
	public static final String DATA_ROOT = ROOT + "data/";
	public static final String OUTPUTS_ROOT = ROOT + "outputs/";
	public static final String MASTER_ROOT = DATA_ROOT + "master";
	public static final String NEW_ROOT = DATA_ROOT + "new";

	public static final String HDFS_HOST = "localhost";
	public static final int HDFS_PORT = 9000;
	public static final URI HDFS_URI = URI.create("hdfs://" + HDFS_HOST + ":" + HDFS_PORT + HDFS_BASE);
	public static final String HDFS_USER = "hadoop";

	public static FileSystem getFs() throws Exception {
		Configuration conf = new Configuration();
		conf.addResource(new Path(CORE_SITE));
		conf.addResource(new Path(HDFS_SITE));
		FileSystem fs = FileSystem.get(HDFS_URI, conf, HDFS_USER);
		return fs;
	}

	public static FileSystem getLocalFs() throws Exception {
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.getLocal(conf);
		return fs;
	}

	public static void resetStoreFiles() throws Exception {
		FileSystem fs = getFs();
		fs.delete(new Path(DATA_ROOT), true);
		fs.mkdirs(new Path(DATA_ROOT));
		Pail.create(fs, FileUtils.NEW_ROOT, PailFormatFactory.getDefaultCopy().setStructure(new DataPailStructure()));
		Pail.create(fs, FileUtils.MASTER_ROOT,
				PailFormatFactory.getDefaultCopy().setStructure(new DataPailStructure()));
	}	
	
	public static void resetOutputFiles() throws Exception {
		FileSystem fs = getFs();
		fs.delete(new Path(OUTPUTS_ROOT), true);
		fs.mkdirs(new Path(OUTPUTS_ROOT));
	}	
	
	public static void resetBatchFiles() throws Exception {
		resetStoreFiles();
		resetOutputFiles();
	}
}
