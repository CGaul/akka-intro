package actors

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import messages._
import scala.concurrent.duration._

/**
  * Created by costa on 3/2/16.
  */
class HazelcastActor(cacheManager: ActorRef) extends Actor with ActorLogging {
  import context.dispatcher


  val config: Config = new Config()
  config.setInstanceName(s"$self")
  val hazelcastInstance = Hazelcast.newHazelcastInstance(config)
  val hazelcastCache = hazelcastInstance.getMap[String, BenchData]("benchCache")

  context.system.scheduler.schedule(5 second, 1 second, self, "tick")

  var readCount: Long = 0
  var writeCount: Long = 0

  override def receive: Receive = {
    case GetBenchData(key) =>
      val benchData: BenchData = hazelcastCache.get(key)
      readCount += 1
      if(benchData != null) {
        log.debug(s"Read benchmark data from Hazelcast: $benchData")
        sender ! ReplyBenchData(benchData)
      }

    case CacheBenchData(key: String, benchData: BenchData) =>
      hazelcastCache.put(key, benchData)
      writeCount += 1
      log.debug(s"Added benchmark data to Hazelcast: $benchData")
      sender ! AckCacheBenchData(key, this.self)

    case "tick" =>
      sendHazelcastStatus()
    }

  def sendHazelcastStatus() = {
    cacheManager ! HazelcastStatus(readCount, writeCount, hazelcastCache.size())
  }
}

object HazelcastActor {
  def props(cacheManager: ActorRef) = Props(new HazelcastActor(cacheManager: ActorRef))
}