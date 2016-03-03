package messages

import akka.actor.ActorRef

/**
  * @author constantin on 2/24/16.
  */
case class HazelcastStatus(readCount: Long, writeCount: Long, cacheSize: Int)

case class RetrieveHazelcastActor(hazelcastActor: ActorRef)

