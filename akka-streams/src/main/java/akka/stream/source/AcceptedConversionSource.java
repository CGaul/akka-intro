package akka.stream.source;

import akka.NotUsed;
import akka.japi.Pair;
import akka.stream.event.RichClickEvent;
import akka.stream.event.RichConversionEvent;
import akka.stream.flow.TimeGroupedEventFlow;
import akka.stream.flow.ZipCombiner;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author by constantin on 6/27/17.
 */
public class AcceptedConversionSource<M> {

    private final Source<RichClickEvent, M> clickEventSource;
    private final Source<RichConversionEvent, M> conversionEventSource;

    public AcceptedConversionSource(Source<RichClickEvent, M> clickEventSource,
                                    Source<RichConversionEvent, M> conversionEventSource) {

        this.clickEventSource = clickEventSource;
        this.conversionEventSource = conversionEventSource;
    }

    public Source<RichConversionEvent, NotUsed> create() {
        TimeGroupedEventFlow<RichClickEvent> clickEventFlow = new TimeGroupedEventFlow<>(10000);

        Source<List<RichClickEvent>, M> normalizedClickSource = clickEventSource
                    .via(clickEventFlow.distinctInWindow())
                    .via(clickEventFlow.groupedWithinTime(10, TimeUnit.SECONDS));

        return new ZipCombiner<List<RichClickEvent>, RichConversionEvent, M>()
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
