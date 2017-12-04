package de.hska.iwi.bdelab.batchqueries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cascading.tap.Tap;
import com.backtype.cascading.tap.PailTap;
import com.backtype.cascading.tap.PailTap.PailTapOptions;
import com.backtype.hadoop.pail.PailSpec;
import com.backtype.hadoop.pail.PailStructure;

import de.hska.iwi.bdelab.batchstore.FileUtils;
import de.hska.iwi.bdelab.schema2.DataUnit;
import manning.tap2.DataPailStructure;
import manning.tap2.SplitDataPailStructure;

import cascading.flow.FlowProcess;
import cascading.operation.FunctionCall;
import cascading.tuple.Tuple;
import cascalog.CascalogFunction;
import jcascalog.Api;
import org.slf4j.Logger;

public class QueryBase {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(QueryBase.class);

    // Hadoop Konfiguration
    public static void setApplicationConf() {
        Map<String, String> conf = new HashMap<String, String>();

        // Mit registriertem Thrift Serializer kann Hadoop Thrift Objekte
        // automatisch serialisieren/deserialisieren
        String sers = "backtype.hadoop.ThriftSerialization";
        sers += ",";
        sers += "org.apache.hadoop.io.serializer.WritableSerialization";
        conf.put("io.serializations", sers);

        // set hadoop user
        //String ugi = FileUtils.HDFS_USER + "," + FileUtils.HDFS_GROUPS;
        String ugi = FileUtils.HDFS_USER;
        log.info("Setting hadoop.job.ugi to '" + ugi + "'");
        conf.put("hadoop.job.ugi", ugi);

        Api.setApplicationConf(conf);
    }

    // Erzeugt Tap Datenquelle aus flachem Fakten-Pail
    @SuppressWarnings("rawtypes")
    public static PailTap dataTap(String path) {
        PailTapOptions opts = new PailTapOptions();
        opts.spec = new PailSpec((PailStructure) new DataPailStructure());
        return new PailTap(path, opts);
    }

    // Erzeugt Tap Datenquelle aus partitioniertem Fakten-Pail
    @SuppressWarnings("rawtypes")
    public static PailTap splitDataTap(String path) {
        PailTapOptions opts = new PailTapOptions();
        opts.spec = new PailSpec((PailStructure) new SplitDataPailStructure());
        return new PailTap(path, opts);
    }

    // Erzeugt Tap Datenquelle für eine Partition aus partitioniertem
    // Fakten-Pail
    @SuppressWarnings({"rawtypes", "unchecked", "serial"})
    public static PailTap attributeTap(String path, final DataUnit._Fields... fields) {
        PailTapOptions opts = new PailTapOptions();
        opts.attrs = new List[]{new ArrayList<String>() {
            {
                for (DataUnit._Fields field : fields) {
                    add("" + field.getThriftFieldId());
                }
            }
        }};
        opts.spec = new PailSpec((PailStructure) new SplitDataPailStructure());

        return new PailTap(path, opts);
    }

    // Nützliche Funktion zur Ausgabe des Streams an beliebiger Stelle einer
    // Querie
    @SuppressWarnings("serial")
    public static class Debug extends CascalogFunction {
        @SuppressWarnings("rawtypes")
        public void operate(FlowProcess process, FunctionCall call) {
            System.out.println("DEBUG: " + call.getArguments().toString());
            call.getOutputCollector().add(new Tuple(1));
        }
    }

}