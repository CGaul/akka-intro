package akka.stream.sink;

import akka.Done;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.stream.event.PlainEventSerialization;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.concurrent.CompletionStage;

/**
 * @author by constantin on 6/29/17.
 */
public class KafkaProducerSink<T> {

    private final String topic;
    private final ProducerSettings<byte[], byte[]> producerSettings;

    public KafkaProducerSink(ProducerSettings<byte[], byte[]> producerSettings, String topic) {
        this.producerSettings = producerSettings;
        this.topic = topic;
    }

    public Sink<T, CompletionStage<Done>> create() {
        return Flow.<T>create()
                .map(event -> {
                    byte[] serializedEvent = PlainEventSerialization.serialize(event);
                    return new ProducerRecord<byte[], byte[]>(topic, serializedEvent);
                })
                .toMat(Producer.plainSink(producerSettings), Keep.right());
    }
}
