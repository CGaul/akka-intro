package akka.stream.flow;

import akka.NotUsed;
import akka.stream.javadsl.Flow;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author by constantin on 6/27/17.
 */
public class WindowGroupingUtils {
    private WindowGroupingUtils() {
    }

    public static<T> Flow<T, T, NotUsed> distinctInWindow(int maxWindowEvents) {
        return Flow.<T>create()
                .grouped(maxWindowEvents)
                .map(eventList -> eventList.stream().distinct().collect(Collectors.toList()))
                .mapConcat(elem -> elem);
    }

}
