package akka.stream.graphs;

import akka.Done;
import akka.NotUsed;
import akka.kafka.ProducerSettings;
import akka.stream.event.PriceEvent;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Source;
import akka.stream.sink.KafkaProducerSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;

/**
 * @author by constantin on 6/29/17.
 */
public class PriceEventGraph {
    private static final Logger LOGGER = LoggerFactory.getLogger(PriceEventGraph.class);

    private final Source<PriceEvent, NotUsed> eventSource;
    private final ProducerSettings<byte[], byte[]> producerSettings;
    private final String topic;

    public PriceEventGraph(Source<PriceEvent, NotUsed> eventSource, ProducerSettings<byte[], byte[]> producerSettings, String topic) {
        this.eventSource = eventSource;
        this.producerSettings = producerSettings;
        this.topic = topic;
    }

    public RunnableGraph<CompletionStage<Done>> create() {
        return eventSource
                .map(event -> {
                    LOGGER.info("produced event: {}", event);
                    return event;
                })
                .toMat(new KafkaProducerSink<PriceEvent>(producerSettings, topic).create(), Keep.right());
    }
}
