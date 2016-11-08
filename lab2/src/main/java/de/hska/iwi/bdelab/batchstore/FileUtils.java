package de.hska.iwi.bdelab.batchstore;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class FileUtils {
	public static final boolean LOCAL = true;
	public static final String NEW_PAIL = "pageviews-new";
	public static final String MASTER_PAIL = "pageviews-master";
	public static final String HDFS_BASE = "/user/hadoop";
	public static final String TMP_ROOT = "/tmp/bdetests";
	public static final String RESULTS = "mapredout";

	public static FileSystem getFs() {
		Configuration conf = new Configuration();
		FileSystem fs = null;
		try {
			if (!LOCAL) {
				conf.addResource(new Path("/usr/local/lib/hadoop-2.7.0/etc/hadoop/core-site.xml"));
				conf.addResource(new Path("/usr/local/lib/hadoop-2.7.0/etc/hadoop/hdfs-site.xml"));
				fs = FileSystem.get(URI.create("hdfs://localhost:9000" + HDFS_BASE), conf, "hadoop");
			} else {
				fs = FileSystem.getLocal(conf);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return fs;
	}

	public static String getTmpPath(FileSystem fs, String name, boolean delete) throws IOException {
		return getPath(fs, TMP_ROOT, name, delete);
	}

	public static String getPath(FileSystem fs, String path, String name, boolean delete) throws IOException {
		String base = LOCAL ? path : HDFS_BASE + path;
		fs.mkdirs(new Path(base));
		String full = base + "/" + name;
		if (fs.exists(new Path(full)) && delete) {
			fs.delete(new Path(full), true);
		}
		return full;
	}

}
