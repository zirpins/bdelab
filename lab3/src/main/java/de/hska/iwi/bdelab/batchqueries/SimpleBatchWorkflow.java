package de.hska.iwi.bdelab.batchqueries;

import org.apache.hadoop.fs.FileSystem;
import com.backtype.hadoop.pail.Pail;
import com.twitter.maple.tap.StdoutTap;
import cascading.flow.FlowProcess;
import cascading.operation.FunctionCall;
import cascading.tap.Tap;
import cascading.tuple.Tuple;
import cascalog.CascalogFunction;
import jcascalog.Api;
import jcascalog.Subquery;

import de.hska.iwi.bdelab.batchstore.FileUtils;
import de.hska.iwi.bdelab.schema2.Data;
import de.hska.iwi.bdelab.schema2.DataUnit;

import java.io.IOException;

public class SimpleBatchWorkflow extends QueryBase {

	// Move newData to master while preserving the newDataPail to keep receiving incoming data
	@SuppressWarnings("rawtypes")
	public static void ingest(Pail masterPail, Pail newDataPail) throws Exception {
        FileSystem fs = FileUtils.getFs(false);

        // create snapshot from newPail
		Pail snapshotPail = newDataPail.snapshot(
		        FileUtils.getTmpPath(fs,"newDataSnapshot",true,false));

		// clone the snapshot
		Pail snapshotCopy = snapshotPail.createEmptyMimic(
		        FileUtils.getTmpPath(fs,"newDataSnapshotCopy",true,false));
		snapshotCopy.copyAppend(snapshotPail);

		// absorb clone into master (the clone will be gone)
		masterPail.absorb(snapshotCopy);

		// clear snapshot from newPail
		newDataPail.deleteSnapshot(snapshotPail);

		// now the snapshot could be deleted as well
		snapshotPail.clear();
	}

	@SuppressWarnings("rawtypes")
	public static void normalizeURLs() throws IOException {
		Tap masterDataset = dataTap(
		        FileUtils.prepareMasterFactsPath(false,false));
		Tap outTap = dataTap(
		        FileUtils.prepareResultsPath("normalized-by-url", true, false));

		Api.execute(outTap, new Subquery("?raw").predicate(masterDataset, "_", "?raw")

		///////////////////////////////////////////////////////////////
		// HIER FEHLT DIE QUERY LOGIK !
		///////////////////////////////////////////////////////////////

		);
	}

	@SuppressWarnings("serial")
	public static class ExtractPageViewFields extends CascalogFunction {
		@SuppressWarnings("rawtypes")
		public void operate(FlowProcess process, FunctionCall call) {
			Data data = ((Data) call.getArguments().getObject(0)).deepCopy();
			DataUnit du = data.get_dataunit();
			if (du.getSetField() == DataUnit._Fields.PAGEVIEW) {
				String url = du.get_pageview().get_page().get_url();
				int time = data.get_pedigree().get_true_as_of_secs();
				call.getOutputCollector().add(new Tuple(url, time));
			}
		}
	}

	@SuppressWarnings("serial")
	public static class ToHour extends CascalogFunction {
		@SuppressWarnings("rawtypes")
		public void operate(FlowProcess process, FunctionCall call) {
			int time = call.getArguments().getInteger(0);
			int hour = time / (60 * 60);
			call.getOutputCollector().add(new Tuple(hour));
		}
	}

	@SuppressWarnings("serial")
	public static class ToGranularityBuckets extends CascalogFunction {
		@SuppressWarnings("rawtypes")
		public void operate(FlowProcess process, FunctionCall call) {
			int hourBucket = call.getArguments().getInteger(0);
			int dayBucket = hourBucket / 24;
			int weekBucket = dayBucket / 7;
			int monthBucket = dayBucket / 28;
			call.getOutputCollector().add(new Tuple("h", hourBucket));
			call.getOutputCollector().add(new Tuple("d", dayBucket));
			call.getOutputCollector().add(new Tuple("w", weekBucket));
			call.getOutputCollector().add(new Tuple("m", monthBucket));
		}
	}

	@SuppressWarnings("rawtypes")
	public static void viewsPerHour() throws IOException {
		Tap normalizedByUrl = dataTap(
		        FileUtils.prepareResultsPath("normalized-by-url", false, false));

		// first query part aggregates views by url and hour
		Subquery hourlyRollup = new Subquery("?url", "?hour-bucket", "?hour-count")
				.predicate(normalizedByUrl, "_", "?fact")
				.predicate(new ExtractPageViewFields(), "?fact").out("?url", "?time")
				.predicate(new ToHour(), "?time").out("?hour-bucket")
				.predicate(new jcascalog.op.Count(), "?hour-count")
				.predicate(new Debug(), "?url", "?hour-bucket", "?hour-count").out("?one");

		// sink into stdout in absence of serving layer db
		Api.execute(new StdoutTap(),
				new Subquery("?url", "?granularity", "?bucket", "?bucket-count")
						.predicate(hourlyRollup, "?url", "?hour-bucket", "?hour-count")
						.predicate(new ToGranularityBuckets(), "?hour-bucket").out("?granularity", "?bucket")
						.predicate(new jcascalog.op.Sum(), "?hour-count").out("?bucket-count"));
	}

	@SuppressWarnings("rawtypes")
	public static void batchWorkflow() throws Exception {
        // Hadoop konfigurieren
        setApplicationConf();

		// Init batch store pails
		Pail masterPail = new Pail(FileUtils.prepareMasterFactsPath(false,false));
		Pail newDataPail = new Pail(FileUtils.prepareNewFactsPath(false,false));

		// Start workflow
		ingest(masterPail, newDataPail);
		normalizeURLs();
		viewsPerHour();
	}

	public static void main(String[] argv) throws Exception {
		batchWorkflow();
	}
}