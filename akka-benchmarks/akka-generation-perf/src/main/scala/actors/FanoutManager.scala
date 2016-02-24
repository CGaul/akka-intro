package actors

import akka.actor.{Props, ActorLogging, Actor}
import messages.{IncubateRequest, StatusReply}
import org.apache.commons.lang.time.StopWatch

object FanoutManager{
  def props(treeWidth: Int, treeDepth: Int):Props = Props(new FanoutManager(treeWidth, treeDepth))
}
/**
  * @author constantin on 2/24/16.
  */
class FanoutManager(treeWidth: Int, treeDepth: Int) extends Actor with ActorLogging {

  val stopWatch : StopWatch = new StopWatch()

  var receivedReplies = 0
  var totalActorCount : Long = 0

  val firstGenerator = context.actorOf(Props(classOf[ChildGenerator]), name = "firstGenerator")
  log.info(s"Starting first Egg-layer at $firstGenerator")

  firstGenerator ! IncubateRequest(treeWidth, treeDepth)
  log.info(s"Sending first incubate request to generator... now kill it before it lays eggs!")
  stopWatch.start()

  def logActorCount(actorCount: Long): Unit = {
    receivedReplies += 1
    totalActorCount += actorCount
    log.info(s"Total actor count: $totalActorCount")
    if(receivedReplies == treeDepth +1){
      stopWatch.stop()
      log.info(s" Max actor count reached after ${stopWatch.getTime} ms.")

      val msPerActor: Float = stopWatch.getTime/totalActorCount.asInstanceOf[Float]
      val usPerActor: Double = Math.round(stopWatch.getTime/totalActorCount.asInstanceOf[Double]*1000)
      log.info(s" Avg. creation time per actor $usPerActor µs ($msPerActor ms).")
      log.info(s" Avg. actors per µs ${Math.round(1f*100f/usPerActor)/100f} actors/µs (${Math.round(1f*100f/msPerActor)/100f} actors/ms).")
      context.system.terminate()
    }
  }

  override def receive: Receive = {
    case StatusReply(actorCount) => logActorCount(actorCount)
  }
}
