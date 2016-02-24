package messages

/**
  * @author constantin on 2/24/16.
  */
trait ChatMessage {}

case class Int1Message(int1 : Int) extends ChatMessage
case class Int2Message(int1 : Int, int2 : Int) extends ChatMessage
case class Int3Message(int1 : Int, int2 : Int, int3: Int) extends ChatMessage

case class Long1Message(long1 : Long) extends ChatMessage
case class Long2Message(long1 : Long, long2 : Long) extends ChatMessage
case class Long3Message(long1 : Long, long2 : Long, long3: Long) extends ChatMessage

case class String1Message(string1 : String) extends ChatMessage
case class String2Message(string1 : String, string2 : String) extends ChatMessage
case class String3Message(string1 : String, string2 : String, string3: String) extends ChatMessage