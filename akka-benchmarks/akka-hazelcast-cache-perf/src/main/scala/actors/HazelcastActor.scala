package actors

import akka.actor.{Props, ActorLogging, Actor}
import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import messages._

/**
  * Created by costa on 3/2/16.
  */
class HazelcastActor extends Actor with ActorLogging {
  val config: Config = new Config()
  config.setInstanceName("hazelcast_benchmark")
  val hazelcastInstance = Hazelcast.newHazelcastInstance(config)
  val hazelcastCache = hazelcastInstance.getMap[String, BenchData]("benchCache")

  override def receive: Receive = {
    case GetBenchData(key) =>
      val benchData: BenchData = hazelcastCache.get(key)
      if(benchData != null) {
        log.debug(s"Read benchmark data from Hazelcast: $benchData")
        sender ! ReplyBenchData(benchData)
      }

    case CacheBenchData(key: String, benchData: BenchData) =>
      hazelcastCache.put(key, benchData)
      log.debug(s"Added benchmark data to Hazelcast: $benchData")
      sender ! AckCacheBenchData(key)
    }
}

object HazelcastActor {
  def props = Props[HazelcastActor]
}