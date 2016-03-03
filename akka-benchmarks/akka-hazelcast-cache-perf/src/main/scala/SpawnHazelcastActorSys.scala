import actors.{ClusterManager, HazelcastActor, BenchDataGenerator}
import akka.actor.{Props, ActorSystem}

/**
  * @author constantin on 2/24/16.
  */
object SpawnHazelcastActorSys extends App{
  val system = ActorSystem("HazelcastPerformanceTest")
  val numberMessages: Long = 5000000
  val clusterSize: Int = 5

  val messagesPerNode = numberMessages/clusterSize

  val benchDataGenerator = system.actorOf(Props(classOf[BenchDataGenerator], messagesPerNode), name = "benchDataGenerator")
  system.log.info(s"Starting BenchDataActor at $benchDataGenerator")

  val clusterManager = system.actorOf(Props(classOf[ClusterManager], benchDataGenerator, clusterSize), name = "clusterManager")
  system.log.info(s"Starting ClusterManager at $clusterManager")
}
