package actors

import akka.actor.{Actor, ActorLogging}
import java.nio.file.Path
import model.IndexPath
import java.io.File
import play.api.Play
import play.api.Play.current
import helpers.Unpacker
import scala.concurrent._
class SourcesIndexer extends Actor with ActorLogging{
  val sourcesLocation = Play.application.configuration.getString("sources.path").getOrElse("sources")
  val unzipper = new Unpacker


  def receive = {
    case IndexPath(path: Path, parentPath: Path) => {
      log.info("Got told to index sources from ${path}")
      if (path.toFile.exists && path.toFile.isFile) {
        val unzip: Future[Boolean] = future {
          unzipper.unZip(path.toAbsolutePath.toString, sourcesLocation)
        }
        unzip.map { x =>
          if (x) {
            log.info("Done unzipping, sending index call to folder indexer")

          } else {
            log.info("Failed to unzip")
          }
        }
      }
    }
  }
}
