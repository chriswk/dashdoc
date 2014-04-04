package actors

import akka.actor.{Actor, ActorLogging}
import com.sksamuel.elastic4s.{KeywordAnalyzer, ElasticClient}
import com.sksamuel.elastic4s.ElasticDsl._

case class GAV(artifactId: String, groupId: String, version: String, classifier: Option[String])

case class Repo(id: String, name: String, url: String)

case class IndexRepo(repo: Repo)

case class CreateRepoIndex()

case class IndexClass(gav: GAV, className: String)

class Indexer extends Actor with ActorLogging {
  val client = ElasticClient.local

  def receive = {
    case IndexRepo(repo) => {
      client.execute {
        index into "repos" -> repo.id fields {
          "id" -> repo.id
          "name" -> repo.name
          "url" -> repo.url
        }
      }
    }

    case IndexClass(gav, className) => {
      client.execute {
        index into "classes" -> gav.artifactId fields {
          "class" -> className
        }
      }
    }

  }
}
