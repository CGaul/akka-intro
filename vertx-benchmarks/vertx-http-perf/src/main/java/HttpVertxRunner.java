import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * @author constantin, on 3/1/16.
 */
public class HttpVertxRunner {

    public static final int NUMBER_VERTICLES = 16;

    public static void main(String[] args) throws Exception {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4jLogDelegateFactory");


        VertxOptions vertxOptions = new VertxOptions().setEventLoopPoolSize(NUMBER_VERTICLES);
        Vertx vertx = Vertx.vertx(vertxOptions);

        DeploymentOptions deployOpts = new DeploymentOptions();
        deployOpts.setInstances(NUMBER_VERTICLES);
        vertx.deployVerticle(HttpVerticle.class.getName(), deployOpts);
    }
}
