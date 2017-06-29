package akka.stream.flow;

import akka.NotUsed;
import akka.stream.event.RichCampaignEvent;
import akka.stream.javadsl.Flow;
import scala.concurrent.duration.FiniteDuration;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author by constantin on 6/27/17.
 */
public class WindowGroupingUtils<T extends RichCampaignEvent> {
    private WindowGroupingUtils(int maxWindowEvents) {
    }

//    public static<T> Flow<T, List<T>, NotUsed> sortInWindow(int maxWindowEvents, int length, TimeUnit time) {
//        return Flow.<T>create()
//                .groupedWithin(maxWindowEvents, FiniteDuration.apply(length, time))
//                .map(list -> list.sort())
//
//    }

    public static<T> Flow<T, T, NotUsed> distinctInWindow(int maxWindowEvents) {
        return Flow.<T>create()
                .grouped(maxWindowEvents)
                .map(eventList -> eventList.stream().distinct().collect(Collectors.toList()))
                .mapConcat(elem -> elem);
    }

}
