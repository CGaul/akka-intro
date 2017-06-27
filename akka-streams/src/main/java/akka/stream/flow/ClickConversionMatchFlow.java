package akka.stream.flow;

import akka.kafka.javadsl.Consumer;
import akka.stream.event.RichClickEvent;
import akka.stream.event.RichConversionEvent;
import akka.stream.javadsl.Source;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author by constantin on 6/27/17.
 */
public class ClickConversionMatchFlow {

    private final Source<RichClickEvent, Consumer.Control> clickEventSource;
    private final Source<RichConversionEvent, Consumer.Control> conversionEventSource;

    public ClickConversionMatchFlow(Source<RichClickEvent, Consumer.Control> clickEventSource,
                                    Source<RichConversionEvent, Consumer.Control> conversionEventSource) {

        this.clickEventSource = clickEventSource;
        this.conversionEventSource = conversionEventSource;
    }

    public void run() {
        TimeGroupedEventFlow<RichClickEvent> clickEventFlow = new TimeGroupedEventFlow<>(10000);
        TimeGroupedEventFlow<RichConversionEvent> convEventFlow = new TimeGroupedEventFlow<>(100);

        //TODO: sources can also be streams?
        Source<Map<String, Long>, Consumer.Control> normalizedClickSource = clickEventSource
                .via(clickEventFlow.distinctInWindow())
                .via(clickEventFlow.groupedWithinTime(10, TimeUnit.SECONDS))
                .via(clickEventFlow.normalizeToMap());

        Source<Map<String, Long>, Consumer.Control> normalizedConvSource = conversionEventSource
                .via(convEventFlow.distinctInWindow())
                .via(convEventFlow.groupedWithinTime(1, TimeUnit.MINUTES))
                .via(convEventFlow.normalizeToMap());

        new ZipCombiner<Map<String, Long>, Map<String, Long>, Consumer.Control>()
                .createFlow(normalizedClickSource, normalizedConvSource);
                //TODO: match both maps here
    }
}
