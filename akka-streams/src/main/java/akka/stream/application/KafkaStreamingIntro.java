package akka.stream.application;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.ProducerSettings;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.event.PriceEvent;
import akka.stream.event.PriceEventGenerator;
import akka.stream.flow.WindowGroupingUtils;
import akka.stream.graphs.DistinctEventsGraph;
import akka.stream.graphs.PriceEventGraph;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Source;
import akka.stream.sink.KafkaProducerSink;
import akka.stream.source.KafkaConsumerSource;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * @author by constantin on 6/27/17.
 */
public class KafkaStreamingIntro {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamingIntro.class);

    public static void main(String[] argv) {
        final ActorSystem system = ActorSystem.create("Kafka-Streaming-Intro");
        final Materializer materializer = ActorMaterializer.create(system);
        final String bootstrapServers = "localhost:9092";
        final ProducerSettings<byte[], byte[]> producerSettings = ProducerSettings
                .create(system, new ByteArraySerializer(), new ByteArraySerializer())
                .withBootstrapServers(bootstrapServers);

        final ConsumerSettings<byte[], byte[]> consumerSettings = ConsumerSettings
                .create(system, new ByteArrayDeserializer(), new ByteArrayDeserializer())
                .withBootstrapServers(bootstrapServers)
                .withGroupId("consumerGroup1")
                .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Source<PriceEvent, NotUsed> eventSource = Source.fromIterator(() -> PriceEventGenerator.generateNDuplicates(1));

        RunnableGraph<CompletionStage<Done>> priceEventTopicGraph =
                new PriceEventGraph(eventSource, producerSettings, "price_events")
                        .create();

        CompletionStage<Done> priceEventTopicStage = priceEventTopicGraph.run(materializer);
        priceEventTopicStage.thenRun(() -> {
            LOGGER.info("All price events produced successfully");
        });

        RunnableGraph<CompletionStage<Done>> sortedPriceEventsGraph =
                new DistinctEventsGraph(producerSettings, consumerSettings, "price_events", "distinct_price_events")
                        .create();

        sortedPriceEventsGraph
                .run(materializer)
                .thenRun(() -> {
                    LOGGER.info("Stage finished successfully");
                    system.terminate();
                });
    }

}
