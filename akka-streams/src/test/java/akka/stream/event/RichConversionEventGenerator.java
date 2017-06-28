package akka.stream.event;

import java.util.UUID;

/**
 * @author by costa on 6/28/17.
 */
public class RichConversionEventGenerator {

    public static RichConversionEvent generate(UUID uuid){
        return new RichConversionEvent(uuid.toString(), "a" , "b", System.currentTimeMillis(), 1);
    }
}
