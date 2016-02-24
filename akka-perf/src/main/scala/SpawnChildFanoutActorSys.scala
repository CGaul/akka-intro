import actors.FanoutObserver
import akka.actor.{Props, ActorSystem}
import org.apache.commons.lang.time.StopWatch

/**
  * Created by costa on 2/24/16.
  */
object SpawnChildFanoutActorSys extends App{
  val system = ActorSystem("ChildFanoutTest")
  val stopWatch: StopWatch = new StopWatch()
  val statusActorRef = system.actorOf(Props[FanoutObserver], name = "statusActor")
  system.log.info(s"Starting Status-Actor at $statusActorRef")
  stopWatch.start()
}
