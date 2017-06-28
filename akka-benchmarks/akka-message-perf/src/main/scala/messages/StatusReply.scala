package messages

/**
  * @author constantin on 2/24/16.
  */
case class StatusRequest()
case class StatusReply(ackedMessages: Long, messagesInFlight: Long)

