package actors

import akka.actor.{Props, ActorLogging, Actor}
import messages.{ChatPartnerSubscription, IncubateRequest, StatusReply}
import org.apache.commons.lang.time.StopWatch

object MessageObserver{
  def props(numberMessages: Long):Props = Props(new MessageObserver(numberMessages))
}
/**
  * @author constantin on 2/24/16.
  */
class MessageObserver(numberMessages: Long) extends Actor with ActorLogging {

  val stopWatch : StopWatch = new StopWatch()

  var receivedReplies = 0
  var totalMessageCount : Long = 0

  val firstGenerator = context.actorOf(Props(classOf[MessageGenerator], numberMessages), name = "firstGenerator")
  log.info(s"Starting first MessageGenerator at $firstGenerator")

  val secondGenerator = context.actorOf(Props(classOf[MessageGenerator], numberMessages), name = "secondGenerator")
  log.info(s"Starting second MessageGenerator at $secondGenerator")

  log.info(s"Pairing both message generators...")
  firstGenerator ! ChatPartnerSubscription(secondGenerator)
  secondGenerator ! ChatPartnerSubscription(firstGenerator)
  stopWatch.start()

  def logMessageCount(messageCount: Long): Unit = {
    receivedReplies += 1
    totalMessageCount += messageCount
    log.info(s"Total message count: $totalMessageCount")
    if(receivedReplies == (MessageGenerator.MAX_CHAT_ROUND+1)*2){
      stopWatch.stop()
      log.info(s" Max message count reached after ${stopWatch.getTime} ms.")

      val msPerActor: Float = totalMessageCount.asInstanceOf[Float]/stopWatch.getTime
      val usPerActor: Double = Math.round(totalMessageCount.asInstanceOf[Double]*100/(stopWatch.getTime*1000))/100d
      log.info(s" Avg. messages-rate per actor $usPerActor messages/µs ($msPerActor messages/ms).")
      context.system.terminate()
    }
  }

  override def receive: Receive = {
    case StatusReply(messageCount) => logMessageCount(messageCount)
  }
}
