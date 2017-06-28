package akka.stream.source;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.event.RichClickEvent;
import akka.stream.event.RichConversionEvent;
import akka.stream.event.RichConversionEventGenerator;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.testkit.TestPublisher;
import akka.stream.testkit.TestSubscriber;
import akka.stream.testkit.javadsl.TestSink;
import akka.stream.testkit.javadsl.TestSource;
import org.junit.Test;

import java.util.Arrays;
import java.util.UUID;

/**
 * @author by constantin on 6/28/17.
 */
public class AcceptedConversionSourceTest {
    private final ActorSystem system = ActorSystem.create("FraudDetectionTest");
    final Materializer materializer = ActorMaterializer.create(system);

    private final Source<RichClickEvent, TestPublisher.Probe<RichClickEvent>> clickProbe = TestSource.probe(system);
    private final Source<RichConversionEvent, TestPublisher.Probe<RichConversionEvent>> convProbe = TestSource.probe(system);

    @Test
    public void testAcceptedConversions() {
        final Source<Integer, NotUsed> sourceUnderTest = Source.single(RichConversionEventGenerator.generate(UUID.randomUUID()))
        Sink<RichConversionEvent, TestSubscriber.Probe<RichConversionEvent>> testSink = TestSink.probe(system);
        Source<RichConversionEvent, NotUsed> source = new AcceptedConversionSource(clickProbe, convProbe).create();
        source
                .runWith(testSink, materializer)
                .request(1)
                .expectComplete();
    }
}
