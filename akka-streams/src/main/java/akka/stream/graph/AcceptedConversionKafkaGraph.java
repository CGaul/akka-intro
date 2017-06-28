package akka.stream.graph;

import akka.Done;
import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.ProducerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.kafka.javadsl.Producer;
import akka.stream.event.PlainEventSerialization;
import akka.stream.event.RichClickEvent;
import akka.stream.event.RichConversionEvent;
import akka.stream.source.AcceptedConversionSource;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Source;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import java.util.concurrent.CompletionStage;

/**
 * @author by constantin on 6/27/17.
 */
public class AcceptedConversionKafkaGraph {
    private final ConsumerSettings<byte[], byte[]> consumerSettings;
    private final ProducerSettings<byte[], byte[]> producerSettings;
    private final String acceptedConversionTopic;

    public AcceptedConversionKafkaGraph(ActorSystem system, String bootstrapServers, String acceptedConversionTopic) {
        this.consumerSettings = ConsumerSettings
                .create(system, new ByteArrayDeserializer(), new ByteArrayDeserializer())
                .withBootstrapServers(bootstrapServers)
                .withGroupId("fraudConversionDetection")
                .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        this.producerSettings = ProducerSettings
                .create(system, new ByteArraySerializer(), new ByteArraySerializer())
                .withBootstrapServers(bootstrapServers);
        this.acceptedConversionTopic = acceptedConversionTopic;
    }

    public RunnableGraph<CompletionStage<Done>> create() {
        Source<RichConversionEvent, Consumer.Control> conversionSource = Consumer
                .plainSource(consumerSettings, Subscriptions.topics("conversions"))
                .map(record -> PlainEventSerialization.<RichConversionEvent>deserialize(record.value()));

        Source<RichClickEvent, Consumer.Control> clickSource = Consumer
                .plainSource(consumerSettings, Subscriptions.topics("clickSource"))
                .map(record -> PlainEventSerialization.<RichClickEvent>deserialize(record.value()));

        return new AcceptedConversionSource(clickSource, conversionSource)
                .create()
                .map(PlainEventSerialization::serialize)
                .map(serializedEvent -> new ProducerRecord<byte[], byte[]>(acceptedConversionTopic, serializedEvent))
                .toMat(Producer.plainSink(producerSettings), Keep.right());
    }
}
