package akka.stream.graphs;

import akka.Done;
import akka.NotUsed;
import akka.kafka.ConsumerSettings;
import akka.kafka.ProducerSettings;
import akka.stream.event.PriceEvent;
import akka.stream.flow.WindowGroupingUtils;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Source;
import akka.stream.sink.KafkaProducerSink;
import akka.stream.source.KafkaConsumerSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * @author by constantin on 6/29/17.
 */
public class DistinctEventsGraph {
    private static final Logger LOGGER = LoggerFactory.getLogger(DistinctEventsGraph.class);

    private final ProducerSettings<byte[], byte[]> producerSettings;
    private final ConsumerSettings<byte[], byte[]> consumerSettings;
    private final String consumerTopic;
    private final String producerTopic;

    public DistinctEventsGraph(ProducerSettings<byte[], byte[]> producerSettings,
                               ConsumerSettings<byte[], byte[]> consumerSettings,
                               String consumerTopic, String producerTopic) {
        this.consumerSettings = consumerSettings;
        this.producerSettings = producerSettings;
        this.consumerTopic = consumerTopic;
        this.producerTopic = producerTopic;
    }

    public RunnableGraph<CompletionStage<Done>> create() {
        return new KafkaConsumerSource<PriceEvent>(consumerSettings, consumerTopic)
                .create()
                .via(WindowGroupingUtils.distinctInWindow(10))
                .map(event -> {
                    LOGGER.info("consumed event: {} for hour {}", event, getHourOfDayFromMillis(event));
                    return event;
                })
                .toMat(new KafkaProducerSink<PriceEvent>(producerSettings, producerTopic).create(), Keep.right());
    }

    private long getHourOfDayFromMillis(PriceEvent event) {
        return TimeUnit.MILLISECONDS.toHours(event.getTimestamp()) % 24;
    }
}
