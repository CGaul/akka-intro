package actors


import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import messages._
import org.apache.commons.lang3.RandomUtils

object ChatActor {
  def props(messagesToBeSent: Long, valByteSize: Int): Props = Props(new ChatActor(messagesToBeSent, valByteSize))
}

/**
  * @author constantin on 2/23/16.
  */
class ChatActor(messagesToBeSent: Long, valByteSize: Int) extends Actor with ActorLogging {

  private var messagesInFlight: Long = 0
  private var ackedMessages: Long = 0
  private var ackedMsgDelta: Long = 0

  private def establishChat(chatPartner: ActorRef) = {
    for (i <- 1L to messagesToBeSent) {
      val chatMessage = ChatMessage(UUID.randomUUID(), RandomUtils.nextBytes(valByteSize))
      messagesInFlight += 1
      chatPartner ! chatMessage
    }
    log.info(s"Done sending all messages to chatPartner $chatPartner")
  }

  private def receiveChatAck() = {
    messagesInFlight -= 1
    ackedMessages += 1
    ackedMsgDelta += 1
    if (ackedMessages == messagesToBeSent) {
      log.info(s"All messages were acknowledged! Shutting down actor $self")
      sendStatusMsg()
    }
  }

  private def sendStatusMsg() = {
    val statusReply = StatusReply(ackedMsgDelta, messagesInFlight)
    context.parent ! statusReply
    ackedMsgDelta = 0
  }

  override def receive: Receive = {
    case ChatPartnerSubscription(partner) => establishChat(partner)
    case ChatMessage(id, _) => context.sender() ! ChatAck(id)
    case ChatAck(_) => receiveChatAck()
    case StatusRequest => sendStatusMsg()
  }
}
