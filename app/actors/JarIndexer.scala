package actors

import akka.actor.{Props, ActorLogging, Actor}
import java.nio.file.Path
import model.{IndexComplete, IndexClass, IndexPath}
import org.clapper.classutil.ClassFinder
import play.api.libs.concurrent.Akka
import play.api.Play.current

class JarIndexer extends Actor with ActorLogging {
  val elasticIndexer = Akka.system.actorOf(Props[ElasticIndexer])
  def receive = {
    case IndexPath(path: Path, rootDir: Path) => {
      log.info(s"Got told to index classes for ${path}")
      val finder = ClassFinder(List(path.toFile))
      finder.getClasses.filter(cl => cl.isConcrete && !cl.name.contains("$")).foreach { f =>
        log.info(s"Indexing ${f}")
        elasticIndexer ! IndexClass(f, rootDir)
      }
    }
    case msg@IndexComplete => {
      log.info(s"Finished indexing ${msg}")
    }
  }
}
