package actors

import akka.actor.{Actor, ActorLogging}
import java.nio.file.Path
import model.IndexPath

class SourcesIndexer extends Actor with ActorLogging{
  def receive = {
    case IndexPath(path: Path) => {
      log.info("Got told to index sources from ${path}")
    }
  }
}
