package de.hska.iwi.vsys.bdelab.streaming;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseBasicBolt;

import java.util.Map;

public abstract class NoisyBolt extends BaseBasicBolt {
    private TopologyContext context;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        this.context = context;
    }

    String getIDs() {
        return context.getThisComponentId() + "(" + context.getThisTaskId() + ")";
    }
}
