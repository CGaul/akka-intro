package akka.stream.source;

import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.event.PlainEventSerialization;
import akka.stream.javadsl.Source;

/**
 * @author by constantin on 6/27/17.
 */
public class KafkaConsumerSource<T> {
    private final ConsumerSettings<byte[], byte[]> consumerSettings;
    private final String topic;

    public KafkaConsumerSource(ConsumerSettings<byte[], byte[]> consumerSettings, String topic) {
        this.consumerSettings = consumerSettings;
        this.topic = topic;
    }

    public Source<T, Consumer.Control> create() {
        return Consumer
                .<byte[], byte[]>plainSource(consumerSettings, Subscriptions.topics(topic))
                .map(record -> PlainEventSerialization.deserialize(record.value()));
    }
}
