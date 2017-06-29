package akka.stream.flow;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.stream.ActorMaterializer;
import akka.stream.Attributes;
import akka.stream.Materializer;
import akka.stream.event.PriceEvent;
import akka.stream.event.PriceEventGenerator;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.testkit.TestSubscriber;
import akka.stream.testkit.javadsl.TestSink;
import org.junit.Test;

/**
 * @author by constantin on 6/29/17.
 */
public class WindowGroupingUtilsTest {
    private final ActorSystem system = ActorSystem.create("FraudDetectionTest");
    final Materializer materializer = ActorMaterializer.create(system);

    @Test
    public void testAcceptedConversions() {
        final Source<PriceEvent, NotUsed> eventSource = Source.fromIterator(() -> PriceEventGenerator.generateNDuplicates(3));
        Sink<PriceEvent, TestSubscriber.Probe<PriceEvent>> testSink = TestSink.probe(system);
        eventSource
                .log("initial produced element", event -> event)
                .via(WindowGroupingUtils.distinctInWindow(3))
                .log("received element after distinct", event -> event)
                .withAttributes(Attributes.logLevels(Logging.InfoLevel(), Logging.InfoLevel(), Logging.ErrorLevel()))
                .runWith(testSink, materializer)
                .request(3)
                .expectNext();
    }
}
