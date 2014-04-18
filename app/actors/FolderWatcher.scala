package actors

import akka.actor.{Props, Actor, ActorLogging}
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import play.Configuration
import com.beachape.filemanagement.MonitorActor
import com.beachape.filemanagement.RegistryTypes._
import com.beachape.filemanagement.Messages._
import model._

class FolderWatcher extends Actor with ActorLogging {
  val fileMonitorActor = Akka.system.actorOf(MonitorActor(concurrency = 5))
  val folderIndexer = Akka.system.actorOf(Props[FolderIndexer])
  val fileIndexer = Akka.system.actorOf(Props[FileIndexer])
  val fileFolderPath = Paths.get(Configuration.root().getString("artifact.path"))
  val folderWatcherCallback: Callback = { path =>
    val msg = IndexPath(path, fileFolderPath)
    log.info(s"Received msg: ${msg}")
    folderIndexer ! msg
  }
  def receive = {
    case WatchFolder(path: Path) => {
      fileMonitorActor ! RegisterCallback(
        ENTRY_CREATE,
        recursive = true,
        path = path,
        folderWatcherCallback
      )
      fileMonitorActor ! RegisterCallback(
        ENTRY_MODIFY,
        recursive = true,
        path = path,
        folderWatcherCallback
      )
    }
  }
}
