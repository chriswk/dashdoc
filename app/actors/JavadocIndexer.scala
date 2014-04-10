package actors

import model._
import akka.actor.{Actor, ActorLogging}
import java.nio.file.Path

class JavadocIndexer extends Actor with ActorLogging {
  def receive = {
    case IndexPath(path: Path, _) => {
      log.info(s"Got told to index javadocs for ${path}")
    }
  }
}
