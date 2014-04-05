package actors

import akka.actor.{Props, Actor, ActorLogging}
import play.api.libs.concurrent.Akka
import java.nio.file.Path
import model.IndexPath

class FileIndexer extends Actor with ActorLogging {
  val jarIndexer = Akka.system.actorOf(Props[JarExtracter])
  val javadocIndexer = Akka.system.actorOf(Props[JavadocIndexer])
  val sourcesIndexer = Akka.system.actorOf(Props[SourcesIndexer])

  def receive = {
    case msg@IndexPath(path: Path) => {
      val f = path.toFile
      if (f.getName().endsWith("pom.xml")) {
        log.info("pom to index")
      } else if (f.getName.endsWith("javadoc.jar")) {
        javadocIndexer ! msg
      } else if (f.getName.endsWith("sources.jar")) {
        sourcesIndexer ! msg
      } else if (f.getName.endsWith(".jar")) {
        jarIndexer ! msg
      } else {
        log.info("Don't know what to do with: ${f}")
      }
    }
  }

}
