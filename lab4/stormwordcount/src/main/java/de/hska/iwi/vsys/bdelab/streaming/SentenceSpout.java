package de.hska.iwi.vsys.bdelab.streaming;

import org.apache.storm.kafka.spout.*;
import org.apache.storm.tuple.Fields;

import java.util.HashMap;
import java.util.Map;

import static org.apache.storm.kafka.spout.KafkaSpoutConfig.FirstPollOffsetStrategy.EARLIEST;
import static org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff.TimeInterval.microSeconds;
import static org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff.TimeInterval.milliSeconds;
import static org.apache.storm.kafka.spout.KafkaSpoutRetryExponentialBackoff.TimeInterval.seconds;

class SentenceSpout extends KafkaSpout<String, String> {
    private static Map<String, Object> props;
    static {
        props = new HashMap<>();
        props.put(KafkaSpoutConfig.Consumer.ENABLE_AUTO_COMMIT, "true");
        props.put(KafkaSpoutConfig.Consumer.BOOTSTRAP_SERVERS, "127.0.0.1:9092");
        props.put(KafkaSpoutConfig.Consumer.GROUP_ID, "kafkaSpoutGroup");
        props.put(KafkaSpoutConfig.Consumer.KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(KafkaSpoutConfig.Consumer.VALUE_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");
    }

    private static final Fields outputFields = new Fields("topic", "partition", "offset", "key", "value");

    private static final String TOPIC_NAME = "sentence";

    static final String STREAM_NAME = "sentence_stream";

    private static final KafkaSpoutStreams streams = new KafkaSpoutStreamsNamedTopics.Builder(
            outputFields, STREAM_NAME, new String[]{TOPIC_NAME}).build();

    private static final KafkaSpoutTuplesBuilder<String, String> tuplesBuilder =
            new KafkaSpoutTuplesBuilderNamedTopics.Builder<>(
                    new TopicTupleBuilder<String, String>(TOPIC_NAME)).build();

    private static final KafkaSpoutRetryService retryService = new KafkaSpoutRetryExponentialBackoff(
            microSeconds(500), milliSeconds(2), Integer.MAX_VALUE, seconds(10));

    private static KafkaSpoutConfig<String, String> config = new KafkaSpoutConfig
            .Builder<>(props, streams, tuplesBuilder, retryService)
            .setOffsetCommitPeriodMs(10000)
            .setFirstPollOffsetStrategy(EARLIEST)
            .setMaxUncommittedOffsets(250)
            .build();

    SentenceSpout() {
        super(config);
    }
}
