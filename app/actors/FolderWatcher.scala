package actors

import akka.actor.{Props, Actor, ActorLogging}
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import com.beachape.filemanagement.MonitorActor
import com.beachape.filemanagement.RegistryTypes._
import com.beachape.filemanagement.Messages._
import model._

class FolderWatcher extends Actor with ActorLogging {
  val fileMonitorActor = Akka.system.actorOf(MonitorActor(concurrency = 2))
  val folderIndexer = Akka.system.actorOf(Props[FolderIndexer])
  val fileIndexer = Akka.system.actorOf(Props[FileIndexer])
  val folderWatcherCallback: Callback = { path =>
    val msg = IndexPath(path, path)
    if (path.toFile.isDirectory) {
      folderIndexer ! msg
    } else {
      fileIndexer ! msg
    }
  }
  def receive = {
    case WatchFolder(path: Path) => {
      fileMonitorActor ! RegisterCallback(
        ENTRY_CREATE,
        None,
        recursive = true,
        path = path,
        folderWatcherCallback
      )
    }
  }
}
