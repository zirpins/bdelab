package de.hska.iwi.bdelab.batchstore;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class FileUtils {

	// change this to your IZ account ID
	// make sure an operator created your hdfs home folder
	public static final String USERID = "abcd1234";

	public static final String NEW_PAIL = "pageviews-new";
	public static final String MASTER_PAIL = "pageviews-master";

	public static final String HDFS_BASE = "/user/" + USERID;
	public static final String LOCAL_BASE = "/tmp/bde";
	public static final String TMP_BASE = "/bdetmp";

	public static final String FACT_BASE = "/facts";
	public static final String RESULT_BASE = "/mapredout";

	public static FileSystem getFs(boolean local) {
		Configuration conf = new Configuration();
		FileSystem fs = null;
		try {
			if (!local) {
				conf.addResource(new Path("/usr/local/opt/hadoop-2.7.3/etc/hadoop/core-site.xml"));
				conf.addResource(new Path("/usr/local/opt/hadoop-2.7.3/etc/hadoop/hdfs-site.xml"));
				fs = FileSystem.get(URI.create("hdfs://193.196.105.68:9000/" + HDFS_BASE), conf, USERID);
			} else {
				fs = FileSystem.getLocal(conf);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return fs;
	}

	public static String getTmpPath(FileSystem fs, String name, boolean delete, boolean local) throws IOException {
		return getPath(fs, TMP_BASE, name, delete, local);
	}

	public static String getPath(FileSystem fs, String path, String name, boolean delete, boolean local) throws IOException {
		String base = local ? LOCAL_BASE + path : HDFS_BASE + path;
		fs.mkdirs(new Path(base));
		String full = base + "/" + name;
		if (fs.exists(new Path(full)) && delete) {
			fs.delete(new Path(full), true);
		}
		return full;
	}

}
