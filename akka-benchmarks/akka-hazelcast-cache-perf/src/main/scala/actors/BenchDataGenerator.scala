package actors

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import messages._
import org.apache.commons.lang.time.StopWatch

object BenchDataGenerator{
  def props(hazelcastActor: ActorRef, numberMessages: Long):Props = Props(new BenchDataGenerator(hazelcastActor, numberMessages))
}
/**
  * @author constantin on 2/24/16.
  */
class BenchDataGenerator(hazelcastActor: ActorRef, numberMessages: Long) extends Actor with ActorLogging {

  val stopWatch : StopWatch = new StopWatch()

  var receivedReplies = 0
  var totalMessageCount : Long = 0

  stopWatch.start()
  sendAnotherDataBench()

//  def logMessageCount(messageCount: Long): Unit = {
//    receivedReplies += 1
//    totalMessageCount += messageCount
//    log.info(s"Total message count: $totalMessageCount")
//    if(receivedReplies == (MessageGenerator.MAX_CHAT_ROUND+1)*2){
//      stopWatch.stop()
//      log.info(s" Max message count reached after ${stopWatch.getTime} ms.")
//
//      val msPerActor: Float = totalMessageCount.asInstanceOf[Float]/stopWatch.getTime
//      val usPerActor: Double = Math.round(totalMessageCount.asInstanceOf[Double]*100/(stopWatch.getTime*1000))/100d
//      log.info(s" Avg. messages-rate per actor $usPerActor messages/µs ($msPerActor messages/ms).")
//      context.system.terminate()
//    }
//  }

  def sendAnotherDataBench(): Unit = {
    val benchDataWithKey : (String, BenchData) = BenchData.generateWithKey()
    hazelcastActor ! CacheBenchData(benchDataWithKey._1, benchDataWithKey._2)
  }

  override def receive: Receive = {
//    case StatusReply(messageCount) => logMessageCount(messageCount)
    case AckCacheBenchData(key) =>
      if(receivedReplies < numberMessages) {
        sendAnotherDataBench()
        receivedReplies += 1
      }
      else {
        stopWatch.stop()
        log.info(s"Message count of $receivedReplies reached after ${stopWatch.getTime} ms.")

        val msPerActor: Float = receivedReplies.asInstanceOf[Float]/stopWatch.getTime
        val usPerActor: Double = Math.round(receivedReplies.asInstanceOf[Double]*100/(stopWatch.getTime*1000))/100d

        log.info(s" Avg. messages-rate per actor $usPerActor messages/µs ($msPerActor messages/ms).")
        context.system.terminate()
      }
  }
}
