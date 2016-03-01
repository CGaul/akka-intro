import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author constantin, on 3/1/16.
 */
public class HttpVerticle extends AbstractVerticle {

    private final static Logger LOG = LoggerFactory.getLogger(HttpVerticle.class);

    private final static String ADDR = "localhost";
    private final static int PORT = 8089;

    private HttpServer httpServer;
    private Router httpRouter;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        this.httpServer = vertx.createHttpServer();
        this.httpRouter = Router.router(vertx);

        Route benchmarkGetRoute = httpRouter.route(HttpMethod.GET, "/benchmark/");
        benchmarkGetRoute.handler(this::replyOk);
        benchmarkGetRoute.failureHandler(routingContext -> routingContext.response().setStatusCode(400).end());

        Route benchmarkPostRoute = httpRouter.route(HttpMethod.POST, "/benchmark/");
        benchmarkPostRoute.handler(this::replyOk);
        benchmarkPostRoute.failureHandler(routingContext -> routingContext.response().setStatusCode(400).end());


        httpServer.requestHandler(httpRouter::accept);
        attemptServerBind(PORT, ADDR, wasBound -> {
            if(wasBound){
                startFuture.complete();
            }
            else {
                startFuture.fail("Http Verticle failed to bind at +"+ADDR+":"+PORT+"!");
            }
        });
    }

    private void replyOk(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        response.putHeader("content-type", "text/plain").setStatusCode(200).end("OK");
    }

    private void attemptServerBind(int port, String addr, Handler<Boolean> wasBoundHandler) {
        this.httpServer.listen(port, addr, serverBindingHandler -> {
            if (serverBindingHandler.succeeded()) {
                LOG.info("successfully bound socket to {}:{}.", addr, port);
                wasBoundHandler.handle(true);
            } else {
                LOG.error("failed to bind: {}", serverBindingHandler.cause());
                wasBoundHandler.handle(false);
            }
        });
    }
}
