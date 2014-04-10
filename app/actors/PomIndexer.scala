package actors

import akka.actor.{Actor, ActorLogging}
import model.IndexPath
import java.nio.file.Path

class PomIndexer extends Actor with ActorLogging {
  def receive = {
    case IndexPath(path: Path, _) => {
      log.info(s"Got told to index pom file: ${path}")
    }
  }
}
