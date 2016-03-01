package actors

import akka.actor.{Props, ActorRefFactory, Actor, ActorLogging}
import spray.routing.HttpService

/**
  * Created by costa on 3/1/16.
  */
class HttpEndpoint extends HttpBenchmarkService with Actor with ActorLogging {
  def receive: Receive = runRoute(rootBenchmarkRoute)

  def actorRefFactory: ActorRefFactory = context
}

object HttpEndpoint {
  def props: Props = Props(new HttpEndpoint())
}


trait HttpBenchmarkService extends HttpService {

  def benchmarkGetRoute = path("benchmark") {
    get {
      complete("OK")
    }
  }
  def benchmarkPostRoute = path("benchmark") {
    post {
      complete("OK")
    }
  }

  def rootBenchmarkRoute = benchmarkGetRoute ~ benchmarkPostRoute
}
