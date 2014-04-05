package actors

import akka.actor.{ActorLogging, Actor}
import java.nio.file.Path
import model.IndexPath

class JarExtracter extends Actor with ActorLogging {
  def receive = {
    case IndexPath(path: Path) => {
      
    }
  }
}
