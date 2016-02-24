import actors.{MessageObserver, FanoutManager}
import akka.actor.{Props, ActorSystem}

/**
  * @author constantin on 2/24/16.
  */
object SpawnMessageActorSys extends App{
  val system = ActorSystem("MessagePerformanceTest")
  val numberMessages: Long = 10000

  val statusActorRef = system.actorOf(Props(classOf[MessageObserver], numberMessages), name = "statusActor")
  system.log.info(s"Starting MessageObserver at $statusActorRef")
}
