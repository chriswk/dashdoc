package actors

import akka.actor.{Props, Actor, ActorLogging}
import model.IndexPath
import play.api.libs.concurrent.Akka
import play.api.Play.current
import java.nio.file.Path

class FolderIndexer extends Actor with ActorLogging {
  val fileIndexer = Akka.system.actorOf(Props[FileIndexer])

  def receive = {
    case msg@IndexPath(path: Path, _) => {
      path.toFile.listFiles.foreach {
        f => {
          if (f.isDirectory) {
            self ! msg
          } else {
            fileIndexer ! msg
          }
        }
      }
    }

  }


}
