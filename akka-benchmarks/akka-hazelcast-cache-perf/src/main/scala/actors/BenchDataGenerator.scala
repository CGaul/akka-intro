package actors

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import messages._
import org.apache.commons.lang.time.StopWatch

object BenchDataGenerator{
  def props(hazelcastActor: ActorRef, numberMessages: Long):Props = Props(new BenchDataGenerator(numberMessages))
}
/**
  * @author constantin on 2/24/16.
  */
class BenchDataGenerator(numberMessages: Long) extends Actor with ActorLogging {

  val stopWatch : StopWatch = new StopWatch()

  var receivedReplies = 0
  var totalMessageCount : Long = 0
  var hazelcastActors: Vector[ActorRef] = Vector()

  stopWatch.start()

  def sendBenchData(hazelcastActor: ActorRef): Unit = {
    for (hazelcastActor <- hazelcastActors) {
      val benchDataWithKey : (String, BenchData) = BenchData.generateWithKey()
      hazelcastActor ! CacheBenchData(benchDataWithKey._1, benchDataWithKey._2)
    }
  }

  override def receive: Receive = {
    case AckCacheBenchData(key, hazelcastActor) =>
      if(receivedReplies < numberMessages) {
        sendBenchData(hazelcastActor)
        receivedReplies += 1
      }
      else {
        stopWatch.stop()
        log.info(s"Message count of $receivedReplies reached after ${stopWatch.getTime} ms.")
        context.system.terminate()
      }

    case RetrieveHazelcastActor(hazelcastActor) =>
      hazelcastActors = hazelcastActors :+ hazelcastActor
      log.info(s"HazelcastActor $hazelcastActor retrieved. Registered actors: ${hazelcastActors.size}")
      log.info(s"Sending first benchData to new $hazelcastActor...")
      sendBenchData(hazelcastActor)
  }
}
