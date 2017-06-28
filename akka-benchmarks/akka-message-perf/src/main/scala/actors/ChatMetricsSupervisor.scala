package actors

import java.util.concurrent.TimeUnit

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume}
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props}
import messages.{ChatPartnerSubscription, StatusReply, StatusRequest}
import org.apache.commons.lang3.time.StopWatch

import scala.collection.immutable.Queue
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


object ChatMetricsSupervisor {
  def props(messagesToBeSentPerActor: Long, numberChatActors: Int, valByteSize: Int): Props =
    Props(new ChatMetricsSupervisor(messagesToBeSentPerActor, numberChatActors, valByteSize))
}

/**
  * @author constantin on 2/24/16.
  */
class ChatMetricsSupervisor(messagesToBeSentPerActor: Long, numberChatActors: Int, valByteSize: Int)
  extends Actor with ActorLogging {
  import context.dispatcher

  private val stopWatch: StopWatch = new StopWatch()
  private var totalMessageCount: Long = 0
  private var chatActors: Queue[ActorRef] = Queue()

  for (i <- 1 to numberChatActors) {
    val actorRef = context.actorOf(
      Props(classOf[ChatActor], messagesToBeSentPerActor, valByteSize),
      name = s"ChatActor_$i")
    chatActors = chatActors :+ actorRef
    log.info(s"Starting ChatActor_$i at $actorRef")
  }

  stopWatch.start()

  val (firstActor, tailQueue) = chatActors.dequeue
  context.system.scheduler.schedule(1 second, 1 second, firstActor, StatusRequest)
  for(tailActor <- tailQueue) {
    firstActor ! ChatPartnerSubscription(tailActor)
    log.info(s"Paired $firstActor with $tailActor.")
    tailActor ! ChatPartnerSubscription(firstActor)
    log.info(s"Paired $tailActor with $firstActor.")
    context.system.scheduler.schedule(1 second, 1 second, tailActor, StatusRequest)
  }

  def logMessageCount(ackedMessageCount: Long, messagesInFlight: Long): Unit = {
    totalMessageCount += ackedMessageCount
    log.info(s"Total acknowledged message count: $totalMessageCount, total messages in flight: $messagesInFlight")
    if (totalMessageCount == numberChatActors * messagesToBeSentPerActor) {
      stopWatch.stop()
      log.info(s" Total message count reached after ${stopWatch.getTime(TimeUnit.SECONDS)} seconds.")

      val totalThroughputMs: Float = totalMessageCount.asInstanceOf[Float] / stopWatch.getTime(TimeUnit.MILLISECONDS)
      val totalThroughputSec: Float = totalMessageCount.asInstanceOf[Float] / stopWatch.getTime(TimeUnit.SECONDS)
      val actorThroughputMs = totalThroughputMs / numberChatActors
      val actorThroughputSec = totalThroughputSec / numberChatActors
      log.info(s" Avg. messages-rate overall $totalThroughputMs messages/ms ($totalThroughputSec messages/sec).")
      log.info(s" Avg. messages-rate per actor $actorThroughputMs messages/ms ($actorThroughputSec messages/sec).")
      context.system.terminate()
    }
  }

  override def receive: Receive = {
    case StatusReply(ackedMessageCount, messagesInFlight) => logMessageCount(ackedMessageCount, messagesInFlight)
  }

  override val supervisorStrategy: OneForOneStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: NullPointerException => Restart
      case _: RuntimeException => Resume
      case _: Exception => Escalate
    }
}
