package actors

import akka.actor.{ActorLogging, Actor}

import play.api.libs.ws.WS

class DocDownloader extends Actor with ActorLogging {
  val repoUrl = "http://repo1.maven.org/maven2"
  def receive = {
    case g@GAV(groupId, artifactId, version, classifier) => {
      WS.url(repoUrl + "/" + g.url + "/" +g.filePath)
    }
  }
}
