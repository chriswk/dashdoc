package actors

import model._
import akka.actor.{Actor, ActorLogging}
import java.nio.file.Path

class JavadocIndexer extends Actor with ActorLogging {
  def receive = {
    case IndexPath(path: Path) => {
      log.info("Got told to index ${path}")
    }
  }
}
