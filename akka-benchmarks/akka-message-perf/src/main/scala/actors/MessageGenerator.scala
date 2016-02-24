package actors


import akka.actor.{ActorLogging, ActorRef, Props, Actor}
import messages._

object MessageGenerator {
  val MAX_CHAT_ROUND = 5
  def props(numberMessages: Long):Props = Props(new MessageGenerator(numberMessages))
}

/**
  * @author constantin on 2/23/16.
  */
class MessageGenerator(numberMessages: Long) extends Actor with ActorLogging {

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

  def getChatRoundMessage(chatRound: Int): ChatMessage = {
    chatRound match {
      case 0 => return Int1Message(Int.MaxValue)
      case 1 => return Int2Message(Int.MaxValue, Int.MaxValue)
      case 2 => return Int3Message(Int.MaxValue, Int.MaxValue, Int.MaxValue)
      case 3 => return Long1Message(Long.MaxValue)
      case 4 => return Long2Message(Long.MaxValue, Long.MaxValue)
      case 5 => return Long3Message(Long.MaxValue, Long.MaxValue, Long.MaxValue)
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
