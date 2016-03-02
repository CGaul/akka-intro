package messages

/**
  * @author constantin on 2/24/16.
  */

case class GetBenchData(key: String)
case class CacheBenchData(key: String, benchData: BenchData)

case class BenchData(String: something)