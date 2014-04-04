package actors

import akka.actor.{Props, Actor, ActorLogging}
import model.{IndexClass, IndexFolder}
import org.clapper.classutil.ClassFinder
import scala.reflect.io.Path
import play.api.libs.concurrent.Akka
import play.api.Play.current

class FileIndexer extends Actor with ActorLogging {
  val indexer = Akka.system.actorOf(Props[Indexer])

  def receive = {
    case IndexFolder(path: String) => {
      val jarFiles = findJarFiles(path)
      jarFiles.map(_.jfile).foreach {
        file =>
          val finder = ClassFinder(List(file))
          finder.getClasses.filter(_.isConcrete).foreach { f =>
            log.info(s"Indexing ${f}")
            indexer ! IndexClass(f)
          }
      }
    }
  }

  def findJarFiles(path: String) = {
    Path(path) walkFilter { p =>
      p.isDirectory || ".jar".r.findFirstIn(p.name).isDefined
    }
  }
}
