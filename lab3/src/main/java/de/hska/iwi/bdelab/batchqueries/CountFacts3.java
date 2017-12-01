package de.hska.iwi.bdelab.batchqueries;

import cascading.tap.Tap;
import de.hska.iwi.bdelab.batchstore.FileUtils;
import jcascalog.Api;
import jcascalog.Subquery;
import jcascalog.op.Count;

// Count Facts in Cascalog (schreibt auf hdfs)
public class CountFacts3 extends QueryBase {

	static Subquery query;

	@SuppressWarnings("rawtypes")
	public static void countFacts() throws Exception {
		// Erzeuge Tap aus Master Pail
		Tap masterDataset = dataTap(FileUtils.prepareMasterFactsPath(false,false));

		// Die Query erzeugt nur einen Zählerwert
		query = new Subquery("?count")
				// Master PailTap generiert Tupel nur mit Data Element
				.predicate(masterDataset, "_", "?raw")
				// Aggregation aller Tupel als Zählerwert
				.predicate(new Count(), "?count");

        // prepare result path
        String resultPath = FileUtils.prepareResultsPath("count-facts-3",true,false);

		// Query ausführen; Ergebnisse gehen als Textzeilen in HDFS File
		Api.execute(Api.hfsTextline(resultPath), query);
	}

	// Dies ist eine Java Anwendung
	public static void main(String[] argv) throws Exception {
		// Hadoop konfigurieren
		setApplicationConf();
		// Query starten
		countFacts();
	}
}
