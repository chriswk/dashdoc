package actors

import akka.actor.{ActorLogging, Actor}

import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._

class DocDownloader extends Actor with ActorLogging {
  val repoUrl = "http://repo1.maven.org/maven2"
  val client = ElasticClient.local
  def receive = {
    case GAV(groupId, artifactId, version, classifier) => {
      client.execute {
        search in "repos"
      }.map(r => {
        val hits = r.getHits
        hits.getHits.foreach { hit =>
          hit.field("url").
        }
      })
    }
  }
}
