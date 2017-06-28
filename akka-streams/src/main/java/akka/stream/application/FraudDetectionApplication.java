package akka.stream.application;

import akka.Done;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.graph.AcceptedConversionKafkaGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;

/**
 * @author by constantin on 6/27/17.
 */
public class FraudDetectionApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(FraudDetectionApplication.class);

    public static void main(String[] argv) {
        final ActorSystem system = ActorSystem.create("FraudDetection");
        final Materializer materializer = ActorMaterializer.create(system);
        final String bootstrapServers = "localhost:9092";

        AcceptedConversionKafkaGraph acceptedConversionKafkaGraph = new AcceptedConversionKafkaGraph(system, bootstrapServers, "accepted_conversions");
        CompletionStage<Done> completionStage = acceptedConversionKafkaGraph
                .create()
                .run(materializer);

        completionStage.thenRun(()-> {
            LOGGER.info("Stage finished successfully");
            system.terminate();
        });
    }
}
