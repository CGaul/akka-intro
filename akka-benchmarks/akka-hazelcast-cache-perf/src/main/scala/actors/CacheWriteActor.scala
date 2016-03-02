package actors


import akka.actor.{ActorLogging, ActorRef, Props, Actor}
import messages._

object CacheWriteActor {
  val MAX_CHAT_ROUND = 5
  def props(numberMessages: Long):Props = Props(new MessageGenerator(numberMessages))
}

/**
  * @author constantin on 2/23/16.
  */
class CacheWriteActor(numberMessages: Long) extends Actor with ActorLogging {

  var childs : Vector[ActorRef] = Vector()
  var statusReplies: Int = 0
  var childCounts: Long = 0

  def establishChat(chatPartner: ActorRef) = {

    for(chatRound <- 0 to MessageGenerator.MAX_CHAT_ROUND){
      val actChatMessage = getChatRoundMessage(chatRound)
      for(i <- 0L to numberMessages) {
        chatPartner ! actChatMessage
      }
      forwardStatus(StatusReply(numberMessages))
    }
  }


  def forwardStatus(statusReply: StatusReply): Unit = {
    // Send the status to the parent, if all direct childs have answered
    context.parent ! statusReply
  }

  override def receive: Receive = {
    case ChatPartnerSubscription(partner) => establishChat(partner)
    case statusReply : StatusReply => forwardStatus(statusReply)
  }
}
