package messages

import akka.actor.ActorRef

/**
  * @author constantin on 2/24/16.
  */

case class GetBenchData(key: String)
case class ReplyBenchData(benchData: BenchData)

case class CacheBenchData(key: String, benchData: BenchData)
case class AckCacheBenchData(key: String, hazelcastActor: ActorRef)

case class BenchData(someData: String)
object BenchData {
  def generate() : BenchData = {
    return new BenchData(randomString(30))
  }

  def generateWithKey() : (String, BenchData) = {
    return (randomString(5), BenchData.generate())
  }

  private def randomString(length: Int) = {
    val r = new scala.util.Random
    val sb = new StringBuilder
    for (i <- 1 to length) {
      sb.append(r.nextPrintableChar)
    }
    sb.toString
  }
}