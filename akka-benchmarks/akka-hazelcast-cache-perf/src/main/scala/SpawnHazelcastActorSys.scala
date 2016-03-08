import actors.{CacheManager, BenchDataGenerator}
import akka.actor.{Props, ActorSystem}

/**
  * @author constantin on 2/24/16.
  */
object SpawnHazelcastActorSys extends App{
  val system = ActorSystem("HazelcastPerformanceTest")
  val numberMessages: Long = 5000000
  val clusterSize: Int = 2

  val messagesPerNode = numberMessages/clusterSize

  val benchDataGenerator = system.actorOf(Props(classOf[BenchDataGenerator], messagesPerNode), name = "benchDataGenerator")
  system.log.info(s"Starting BenchDataActor at $benchDataGenerator")

  val cacheManager = system.actorOf(Props(classOf[CacheManager], benchDataGenerator, clusterSize), name = "cacheManager")
  system.log.info(s"Starting CacheManager at $cacheManager")
}
