package actors

import akka.actor.{Props, ActorLogging, Actor}
import messages.{IncubateRequest, StatusReply}
import org.apache.commons.lang.time.StopWatch

object FanoutObserver{
  def props():Props = Props(new FanoutObserver())
}
/**
  * Created by costa on 2/24/16.
  */
class FanoutObserver extends Actor with ActorLogging {

  val stopWatch : StopWatch = new StopWatch()
  val width = 10
  val fanout = 6

  var receivedReplies = 0
  var totalActorCount : Long = 0

  val firstGenerator = context.actorOf(Props(classOf[ChildGenerator]), name = "firstGenerator")
  log.info(s"Starting first Egg-layer at $firstGenerator")

  firstGenerator ! IncubateRequest(width, fanout)
  log.info(s"Sending first incubate request to generator... now kill it before it lays eggs!")
  stopWatch.start()

  def logActorCount(actorCount: Long): Unit = {
    receivedReplies += 1
    totalActorCount += actorCount
    log.info(s"Total actor count: $totalActorCount")
    if(receivedReplies == fanout +1){
      stopWatch.stop()
      log.info(s" Max actor count reached after ${stopWatch.getTime} ms.")
      context.system.terminate()
    }
  }

  override def receive: Receive = {
    case StatusReply(actorCount) => logActorCount(actorCount)
  }
}
