import actors.FanoutManager
import akka.actor.{Props, ActorSystem}

/**
  * @author constantin on 2/24/16.
  */
object SpawnChildFanoutActorSys extends App{
  val system = ActorSystem("FanoutPerformanceTest")

  val treeWidth = 10
  val treeDepth = 5

  val statusActorRef = system.actorOf(Props(classOf[FanoutManager], treeWidth, treeDepth), name = "statusActor")
  system.log.info(s"Starting FanoutManager at $statusActorRef")
}
