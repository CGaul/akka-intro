package akka.stream.flow;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.japi.Pair;
import akka.kafka.ConsumerSettings;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Consumer;
import akka.stream.event.RichCampaignEvent;
import akka.stream.event.RichClickEvent;
import akka.stream.event.RichConversionEvent;
import akka.stream.javadsl.Source;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import scala.concurrent.duration.FiniteDuration;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author by constantin on 6/27/17.
 */
public class ConversionFlow {

    private final ActorSystem system;
    private final ConsumerSettings<byte[], String> consumerSettings;
    private final ProducerSettings<byte[], String> producerSettings;

    private final Source<RichClickEvent, Consumer.Control> clickEventSource;
    private final Source<RichConversionEvent, Consumer.Control> conversionEventSource;

    public ConversionFlow(ActorSystem system,
                          Source<RichClickEvent, Consumer.Control> clickEventSource,
                          Source<RichConversionEvent, Consumer.Control> conversionEventSource, String bootstrapServers) {
        this.system = system;
        this.consumerSettings = ConsumerSettings
                .create(system, new ByteArrayDeserializer(), new StringDeserializer())
                .withBootstrapServers(bootstrapServers)
                .withGroupId("group1")
                .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        this.producerSettings = ProducerSettings
                .create(system, new ByteArraySerializer(), new StringSerializer())
                .withBootstrapServers(bootstrapServers);

        this.clickEventSource = clickEventSource;
        this.conversionEventSource = conversionEventSource;
    }

    public void bla() {
        new ZipCombiner<List<Pair<Long, String>>, List<Pair<Long, String>>, Consumer.Control>()
                .createFlow(clicks(), conversions())
                .map()
    }

    public Source<HashMap<String, Long>, Consumer.Control> clicks() {
//        Source<ConsumerRecord<byte[], String>, Consumer.Control> consumerSource =
//                Consumer.plainSource(consumerSettings, Subscriptions.topics("conversions"));

        return clickEventSource
                .map(clickEvent -> new Pair<>(clickEvent.timestamp, clickEvent.rid))
                .groupedWithin(50, FiniteDuration.apply(100, TimeUnit.MILLISECONDS))
                .map(event -> {
                    HashMap<String, Long> ridTimestampMap = new HashMap<>(event.size());
                    event.forEach(entry -> ridTimestampMap.put(entry.second(), entry.first()));
                    return ridTimestampMap;
                });
    }

    public Source<List<Pair<Long, String>>, Consumer.Control> conversions() {
        return conversionEventSource
                .map(convEvent -> new Pair<>(convEvent.timestamp, convEvent.rid))
                .groupedWithin(50, FiniteDuration.apply(250, TimeUnit.MILLISECONDS));
    }
}
