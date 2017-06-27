package akka.stream.application;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.flow.ClickConversionMatchFlow;

/**
 * @author by constantin on 6/27/17.
 */
public class FraudDetectionApplication {


    public static void main(String[] argv) {
        final ActorSystem system = ActorSystem.create("FraudDetection");
        final Materializer materializer = ActorMaterializer.create(system);
        final String bootstrapServers = "localhost:9092";

        ClickConversionMatchFlow clickConversionMatchFlow = new ClickConversionMatchFlow(system, bootstrapServers);
    }
}
