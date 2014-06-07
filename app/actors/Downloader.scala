package actors

import model._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.concurrent.Akka
import akka.actor.{Props, ActorLogging, Actor}
import play.api.Play.current
import play.api.libs.ws._
import play.Configuration
import java.io.{File, BufferedOutputStream, FileOutputStream, OutputStream}
import play.api.libs.iteratee.{Done, Input, Cont, Iteratee}

class Downloader extends Actor with ActorLogging {
  val repoUrl = "http://repo1.maven.org/maven2"
  val fileFolderPath = Configuration.root().getString("artifact.path")
  val fileFolder = new File(fileFolderPath)

  def receive = {
    case g@GAV(groupId, artifactId, version, classifier) => {
      val downloadPath = new File(fileFolder, g.url)
      downloadPath.mkdirs()
      val outputStream = new BufferedOutputStream(new FileOutputStream(new File(downloadPath, g.filePath)))
      val response = WS.url(s"${repoUrl}/${g.downloadUrl}").get {
        headers => fromStream(outputStream)
      }.flatMap(_.run)
    }
  }

  def fromStream(stream: OutputStream): Iteratee[Array[Byte], Unit] = Cont {
    case e@Input.EOF =>
      stream.close()
      Done((), e)
    case Input.El(data) =>
      stream.write(data)
      fromStream(stream)
    case Input.Empty =>
      fromStream(stream)
  }
}
