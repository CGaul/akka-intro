import actors.HttpEndpoint
import akka.actor.{Props, ActorSystem}
import akka.io.IO
import spray.can.Http

/**
  * @author constantin on 2/24/16.
  */
object HttpPerfActorSys extends App{
  implicit val system = ActorSystem("HttpPerformanceTest")
  val numberMessages: Long = 10000

  val httpEndpoint = system.actorOf(Props(classOf[HttpEndpoint]), name = "httpEndpointActor")
  system.log.info(s"Starting Http Endpoint at $httpEndpoint")

  IO(Http) ! Http.Bind(httpEndpoint, interface = "0.0.0.0", port = 8088)

}
