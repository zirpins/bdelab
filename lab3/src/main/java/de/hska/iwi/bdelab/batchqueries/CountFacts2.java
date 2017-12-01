package de.hska.iwi.bdelab.batchqueries;

import com.twitter.maple.tap.StdoutTap;

import cascading.flow.Flow;
import cascading.tap.Tap;
import de.hska.iwi.bdelab.batchstore.FileUtils;
import jcascalog.Api;
import jcascalog.Subquery;
import jcascalog.op.Count;

// Count Facts in Cascalog (schreibt nach stdout)
public class CountFacts2 extends QueryBase {

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

		// Query ausführen; Ergebnis wird an Stdout gesendet
		Api.execute(new StdoutTap(), query);

		///////////////////////////////////////////////////////////////
		// Optional:
		// Die Querry kann auch als Cascading Flow kompiliert werden
		Flow flow = Api.compileFlow(new StdoutTap(), query);

		// Eine Grafik des Cascading Flows erzeugen (ähnlich Pipe Diagramm)
		flow.writeDOT("/tmp/count_fact_flow.dot");

		// Alternative Möglichkeit, die Query auszuführen
		// flow.complete();
		///////////////////////////////////////////////////////////////
	}

	// Dies ist eine Java Anwendung
	public static void main(String[] argv) throws Exception {
		// Hadoop konfigurieren
		setApplicationConf();
		// Query starten
		countFacts();
	}
}
