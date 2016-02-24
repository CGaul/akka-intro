package actors

import akka.actor.{ActorLogging, ActorRef, Props, Actor}
import messages.{StatusReply, IncubateRequest}

object ChildGenerator {
  def props():Props = Props(new ChildGenerator())
}

/**
  * Created by costa on 2/23/16.
  */
class ChildGenerator() extends Actor with ActorLogging {

  var childs : Vector[ActorRef] = Vector()
  var statusReplies: Int = 0
  var childCounts: Long = 0

  /**
    * Lay eggs! Spawn seed amount of child actors.
    *
    * @param seed
    */
  def layEggs(seed: Int, maxFanout: Int): Unit = {
    if(maxFanout > 0) {
      for (i <- 1 to seed) {
        val childRef = context.actorOf(Props(classOf[ChildGenerator]), name = s"child-$i")
        childs = childs :+ childRef

        childRef ! IncubateRequest(seed, maxFanout - 1)
      }
      context.parent ! StatusReply(seed)
    }
    else {
      log.debug(s"Max fanout for actor ${this.self} reached.")
      context.parent ! StatusReply(0)
    }
  }

  def escalateStatus(statusReply: StatusReply): Unit = {
    // Send the status to the parent, if all direct childs have answered
    childCounts += statusReply.actorCount
    statusReplies += 1
    if(statusReplies == childs.size){
      context.parent ! StatusReply(childCounts)
      childCounts = 0
      statusReplies = 0
    }
  }

  override def receive: Receive = {
    case IncubateRequest(seed, maxFanout) => layEggs(seed, maxFanout)
    case statusReply : StatusReply => escalateStatus(statusReply)
  }
}
