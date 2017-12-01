package de.hska.iwi.bdelab.batchqueries.examples;

import cascading.flow.FlowProcess;
import cascading.operation.FunctionCall;
import cascading.tuple.Tuple;
import cascalog.CascalogFunction;

@SuppressWarnings("serial")
public class Split extends CascalogFunction {
	@SuppressWarnings("rawtypes")
	public void operate(FlowProcess flowProcess, FunctionCall fnCall) {
		String sentence = fnCall.getArguments().getString(0);
		for (String word : sentence.split(" ")) {
			fnCall.getOutputCollector().add(new Tuple(word));
		}
	}
}