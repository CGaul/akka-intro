package actors

/**
  * Created by costa on 3/2/16.
  */
class HazelcastActor extends Actor with ActorLogging {
  val config: Config = new Config()
  config.setInstanceName("hazelcast_benchmark")
  val hazelcastInstance = Hazelcast.newHazelcastInstance(config)
  val rtProfileCache = hazelcastInstance.getMap[String, BenchData]("benchCache")

  override def receive: Receive = {
    case GetProfile(deviceId) =>
      val rtProfile: RTProfile = rtProfileCache.get(deviceId)
      if(rtProfile != null) {
        log.info(s"Read realtime profile from Hazelcast: $rtProfile")
        sender ! rtProfileCache.get(deviceId)
      }

    case CacheProfile(rtProfile) =>
      rtProfileCache.put(rtProfile.deviceId, rtProfile)
      log.info(s"Added realtime profile to Hazelcast: $rtProfile")
      sender ! AckCacheProfile(rtProfile.deviceId)
    }
}

object HazelcastActor {
  def props = Props[HazelcastActor]
}