import actors.ChatMetricsSupervisor
import akka.actor.{Props, ActorSystem}

/**
  * @author constantin on 2/24/16.
  */
object SpawnMessageActorSys extends App {
  val system = ActorSystem("MessagePerformanceTest")
  val messagesToBeSentPerActor: Long = 1000000
  val numberActors: Int = 2
  val valByteSize: Int = 100

  val chatMetricsSupervisor = system.actorOf(
    Props(classOf[ChatMetricsSupervisor], messagesToBeSentPerActor, numberActors, valByteSize),
    name = "chatMetricsSupervisor")
  system.log.info(s"Starting ChatMetricsSupervisor at $chatMetricsSupervisor")
}
