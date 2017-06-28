package messages

import java.util.UUID

/**
  * @author constantin on 2/24/16.
  */
case class ChatMessage(id: UUID, value: Array[Byte])
case class ChatAck(id: UUID)