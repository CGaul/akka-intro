import actors.{HazelcastActor, BenchDataGenerator}
import akka.actor.{Props, ActorSystem}

/**
  * @author constantin on 2/24/16.
  */
object SpawnHazelcastActorSys extends App{
  val system = ActorSystem("HazelcastPerformanceTest")
  val numberMessages: Long = 1000000

  val hazelcastActor = system.actorOf(Props(classOf[HazelcastActor]), name = "hazelcastActor")
  system.log.info(s"Starting HazelcastActor at $hazelcastActor")

  val benchDataActor = system.actorOf(Props(classOf[BenchDataGenerator], hazelcastActor, numberMessages), name = "benchDataActor")
  system.log.info(s"Starting BenchDataActor at $benchDataActor")
}
