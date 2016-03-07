package actors

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import messages.{RetrieveHazelcastActor, HazelcastStatus}
import org.apache.commons.lang.time.StopWatch
import scala.concurrent.duration._


/**
  * Created by constantin on 3/3/16.
  */
class CacheManager(benchDataGenerator: ActorRef, clusterSize: Int) extends Actor with ActorLogging {
  import context.dispatcher


  val stopWatch: StopWatch = new StopWatch()

  var totalReads, totalWrites: Long = 0

  var avgReads, avgWrites: Long = 0
  var currentCache: Int = 0

  startCluster()


  def startCluster() = {
    context.system.scheduler.schedule(5 second, 1 second, self, "tick")
    stopWatch.start()
    for (i <- 1 to clusterSize ) {
      val hazelcastActor = context.actorOf(Props(classOf[HazelcastActor], this.self), name = s"hazelcastActor-$i")
      log.info(s"Starting hazelcastActor-$i at $hazelcastActor")

      log.info(s"Broadcasting HazelcastActor to $benchDataGenerator...")
      benchDataGenerator ! RetrieveHazelcastActor(hazelcastActor)
    }
  }


  def mergeStatus(read: Long, write: Long, cacheSize: Int): Unit = {
    totalReads += read
    totalWrites += write

    avgReads = totalReads / clusterSize
    avgWrites = totalWrites / clusterSize
    currentCache = cacheSize
  }

  def logStatus() = {
    val readsPerSec = totalReads/stopWatch.getTime * 1000
    val writesPerSec = totalWrites/stopWatch.getTime * 1000
    log.info(s"Hazelcast Nodes $clusterSize \n with total Hazelcast-Cache size: $currentCache" +
      s"Total Reads: $totalReads, total writes: $totalWrites, \n" +
      s"Avg. reads per node: $avgReads, avg. writes per node: $avgWrites, \n" +
      s"reads/sec: $readsPerSec, writes/sec: $writesPerSec")
  }

  override def receive: Receive = {
    case HazelcastStatus(read, write, cacheSize) => mergeStatus(read, write, cacheSize)
    case "tick" => logStatus()
  }
}

object CacheManager {
  def props(benchDataGenerator: ActorRef, clusterSize: Int) = Props(new CacheManager(benchDataGenerator, clusterSize))
}
