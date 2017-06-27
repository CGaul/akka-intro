package akka.stream.flow;

import akka.NotUsed;
import akka.japi.Pair;
import akka.kafka.javadsl.Consumer;
import akka.stream.event.RichClickEvent;
import akka.stream.event.RichConversionEvent;
import akka.stream.javadsl.Source;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    //TODO: sources can also be flows?
    public Source<RichConversionEvent, NotUsed> run() {
        TimeGroupedEventFlow<RichClickEvent> clickEventFlow = new TimeGroupedEventFlow<>(10000);

        Source<List<RichClickEvent>, Consumer.Control> normalizedClickSource = clickEventSource
                .via(clickEventFlow.distinctInWindow())
                .via(clickEventFlow.groupedWithinTime(10, TimeUnit.SECONDS));

        return new ZipCombiner<List<RichClickEvent>, RichConversionEvent, Consumer.Control>()
                .createFlow(normalizedClickSource, conversionEventSource)
                .filter(clicksConversionPair -> {
                    List<RichClickEvent> clickList = clicksConversionPair.first();
                    RichConversionEvent conversion = clicksConversionPair.second();
                    return clickList.stream()
                                    .map(clickEntry -> clickEntry.rid)
                                    .collect(Collectors.toList())
                                    .contains(conversion.rid);
                })
                .map(Pair::second);
    }
}
