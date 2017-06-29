package akka.stream.event;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

/**
 * @author by costa on 6/28/17.
 */
public class PriceEventGenerator {

    private PriceEventGenerator() {
    }

    private static final Random RANDOM = new Random();

    public static PriceEvent generate(UUID uuid) {
        int pay = RANDOM.nextInt(100);
        return new PriceEvent(uuid.toString(), getRandomTimestamp().getTime(), pay);
    }

    public static Iterator<PriceEvent> generateN(int n) {
        Collection<PriceEvent> priceEventList = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            priceEventList.add(generate(UUID.randomUUID()));
        }
        return priceEventList.iterator();
    }

    public static Iterator<PriceEvent> generateNDuplicates(int n) {
        PriceEvent priceEvent = generate(UUID.randomUUID());
        Collection<PriceEvent> priceEventList = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            priceEventList.add(priceEvent);
        }
        return priceEventList.iterator();
    }

    private static Timestamp getRandomTimestamp() {
        long offset = Timestamp.valueOf("2017-06-28 00:00:00").getTime();
        long end = Timestamp.valueOf("2017-06-29 00:00:00").getTime();
        long diff = end - offset + 1;
        return new Timestamp(offset + (long) (Math.random() * diff));
    }
}
