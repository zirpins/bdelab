package de.hska.iwi.bdelab.batchstore;

import java.io.IOException;
import java.net.URI;

import com.backtype.hadoop.pail.Pail;
import com.backtype.hadoop.pail.PailFormatFactory;
import manning.tap.DataPailStructure;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;

public class FileUtils {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(FileUtils.class);

    // change this to your IZ account ID
    // make sure an operator created your hdfs home folder
    private static final String HDFS_USER = "zich0001";

    private static final String NEW_PAIL = "pageviews-new";
    private static final String MASTER_PAIL = "pageviews-master";

    private static final String HDFS_BASE = "/user/" + HDFS_USER;
    private static final String LOCAL_BASE = "/tmp/bde";
    private static final String TMP_BASE = "bdetmp";

    private static final String FACT_BASE = "facts";
    private static final String RESULT_BASE = "mapredout";

    private static final String CORE_SITE = "/usr/local/lib/hadoop-2.7.0/etc/hadoop/core-site.xml";
    private static final String HDFS_SITE = "/usr/local/lib/hadoop-2.7.0/etc/hadoop/hdfs-site.xml";

    private static final String HDFS_HOST = "193.196.105.68";
    private static final int HDFS_PORT = 9000;
    private static final URI HDFS_URI = URI.create("hdfs://" + HDFS_HOST + ":" + HDFS_PORT + HDFS_BASE);

    public static FileSystem getFs(boolean local) {
        Configuration conf = new Configuration();
        FileSystem fs = null;
        try {
            if (!local) {
                conf.addResource(new Path(CORE_SITE));
                conf.addResource(new Path(HDFS_SITE));
                fs = FileSystem.get(HDFS_URI, conf, HDFS_USER);
            } else {
                fs = FileSystem.getLocal(conf);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return fs;
    }

    public static void resetBatchFiles() throws IOException {
        resetStoreFiles();
        prepareResultsPath(null,true, false);
    }

    private static void resetStoreFiles() throws IOException {
        FileSystem fs = FileUtils.getFs(false);

        String masterPathName = prepareMasterFactsPath(true,false);
        String newPathName = prepareNewFactsPath(true, false);

        Pail.create(fs, newPathName,
                PailFormatFactory.getDefaultCopy().setStructure(new DataPailStructure()));
        Pail.create(fs, masterPathName,
                PailFormatFactory.getDefaultCopy().setStructure(new DataPailStructure()), false);
    }

    public static String prepareMasterFactsPath(boolean delete, boolean local) throws IOException {
        FileSystem fs = FileUtils.getFs(false);
        return getPath(fs, FileUtils.FACT_BASE, FileUtils.MASTER_PAIL, delete, local);
    }

    public static String prepareNewFactsPath(boolean delete, boolean local) throws IOException {
        FileSystem fs = FileUtils.getFs(false);
        return getPath(fs, FileUtils.FACT_BASE, FileUtils.NEW_PAIL, delete, local);
    }

    private static String prepareResultsPath(String resultType, boolean delete, boolean local) throws IOException {
        FileSystem fs = FileUtils.getFs(local);
        return getPath(fs, FileUtils.RESULT_BASE, resultType, delete, local);
    }

    public static String getTmpPath(FileSystem fs, String name, boolean delete, boolean local) throws IOException {
        return getPath(fs, TMP_BASE, name, delete, local);
    }

    private static String getPath(FileSystem fs, String pathName, String fileName, boolean delete, boolean local) throws IOException {
        String fqpn = (local ? LOCAL_BASE : HDFS_BASE) + Path.SEPARATOR + pathName;
        if (fileName != null)
            fqpn = fqpn + Path.SEPARATOR + fileName;
        log.info("Preparing FQPN '" + fqpn + "' which is " + (delete ? "to" : "not to") + " be deleted");
        Path fqp = new Path(fqpn);
        if (fs.exists(fqp) && delete) {
            log.debug("Deleting FQPN '" + fqpn + "'");
            fs.delete(fqp, true);
        } else {
            log.info("Preparing FQPN parent dir '" + fqp.getParent().toUri().getPath() + "'");
            fs.mkdirs(fqp.getParent());
        }
        return fqpn;
    }

}