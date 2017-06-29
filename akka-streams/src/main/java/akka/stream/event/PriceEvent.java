package akka.stream.event;

import java.io.Serializable;

/**
 * @author by constantin on 6/27/17.
 */
public class PriceEvent implements Serializable {
    private final String id;
    private final long timestamp;
    private final long pay;

    public PriceEvent(String id, long timestamp, long pay) {
        this.id = id;
        this.timestamp = timestamp;
        this.pay = pay;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "PriceEvent{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", pay=" + pay +
                '}';
    }
}
