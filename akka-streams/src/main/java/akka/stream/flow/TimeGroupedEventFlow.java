package akka.stream.flow;

import akka.NotUsed;
import akka.japi.Pair;
import akka.stream.event.RichCampaignEvent;
import akka.stream.javadsl.Flow;
import scala.concurrent.duration.FiniteDuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author by constantin on 6/27/17.
 */
public class TimeGroupedEventFlow<T extends RichCampaignEvent> {

    private final int maxWindowEvents;

    public TimeGroupedEventFlow(int maxWindowEvents) {
        this.maxWindowEvents = maxWindowEvents;
    }

    public Flow<T, List<T>, NotUsed> groupedWithinTime(int length, TimeUnit time) {
        return Flow.<T>create()
                .groupedWithin(maxWindowEvents, FiniteDuration.apply(length, time));
    }

    public Flow<List<T>, Map<String, Long>, NotUsed> normalizeToMap() {
        return Flow.<List<T>>create()
                .map(event -> {
                    HashMap<String, Long> ridTimestampMap = new HashMap<>(event.size());
                    event.forEach(entry -> ridTimestampMap.put(entry.rid, entry.timestamp));
                    return ridTimestampMap;
                });
    }

    public Flow<T, T, NotUsed> distinctInWindow() {
        return Flow.<T>create()
                .grouped(maxWindowEvents)
                .map(eventList -> eventList.stream().distinct().collect(Collectors.toList()))
                .mapConcat(elem -> elem);
    }

}
