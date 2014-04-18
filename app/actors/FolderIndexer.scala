package actors

import akka.actor.{Props, Actor, ActorLogging}
import model.IndexPath
import play.api.libs.concurrent.Akka
import play.api.Play.current
import java.nio.file.Path

class FolderIndexer extends Actor with ActorLogging {
  val fileIndexer = Akka.system.actorOf(Props[FileIndexer])

  def receive = {
    case msg@IndexPath(path: Path, rootDir: Path) => {
      log.info(s"Indexing $msg")
      path.toFile.listFiles.filter(_.toAbsolutePath != rootDir.toAbsolutePath).foreach {
        f => {
          if (f.isDirectory) {
            self ! IndexPath(f.toPath, rootDir)
          } else {
            fileIndexer ! IndexPath(f.toPath, rootDir)
          }
        }
      }
    }

  }


}
