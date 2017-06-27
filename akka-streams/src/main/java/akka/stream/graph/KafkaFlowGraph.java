package akka.stream.graph;

import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.ProducerSettings;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * @author by constantin on 6/27/17.
 */
public class KafkaFlowGraph {
    private final ConsumerSettings<byte[], String> consumerSettings;
    private final ProducerSettings<byte[], String> producerSettings;

    public KafkaFlowGraph(ActorSystem system, String bootstrapServers) {
        this.consumerSettings = ConsumerSettings
                .create(system, new ByteArrayDeserializer(), new StringDeserializer())
                .withBootstrapServers(bootstrapServers)
                .withGroupId("group1")
                .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        this.producerSettings = ProducerSettings
                .create(system, new ByteArraySerializer(), new StringSerializer())
                .withBootstrapServers(bootstrapServers);
    }


}
