package de.hska.iwi.bdelab.batchstore;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class FileUtils {

	public static final String HDFS_BASE = "/user/hadoop";
	public static final String TMP_ROOT = "/tmp/bdetests";

	public static FileSystem getFs(boolean isLocal) {
		Configuration conf = new Configuration();
		FileSystem fs = null;
		try {
			if (!isLocal) {
				//conf.addResource(new Path("/usr/local/lib/hadoop-2.7.0/etc/hadoop/core-site.xml"));
				//conf.addResource(new Path("/usr/local/lib/hadoop-2.7.0/etc/hadoop/hdfs-site.xml"));
				fs = FileSystem.get(URI.create("hdfs://127.0.0.1:9000/" + HDFS_BASE), conf, "hadoop");
			} else {
				fs = FileSystem.getLocal(conf);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return fs;
	}

	public static String getTmpPath(FileSystem fs, String name, boolean local) throws IOException {
		return getPath(fs,TMP_ROOT,name,local);
	}
	
	public static String getPath(FileSystem fs, String path, String name, boolean local) throws IOException {
		String base = local ? path : HDFS_BASE + path;
		fs.mkdirs(new Path(base));
		String full = base + "/" + name;
		if (fs.exists(new Path(full))) {
			fs.delete(new Path(full), true);
		}
		return full;
	}

}
