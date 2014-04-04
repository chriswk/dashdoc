package actors

import play.api.libs.concurrent.Execution.Implicits._
import akka.actor.{Actor, ActorLogging}
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import model._
import play.Configuration

class Indexer extends Actor with ActorLogging {
  val c = Configuration.root()
  val client = {
    val url = c.getString("elasticsearch.url")
    val port = c.getInt("elasticsearch.port")
    ElasticClient.remote(url, port)
  }

  def receive = {
    case IndexRepo(repo) => {
      client.execute {
        index into "repos" -> repo.id fields {
          "id" -> repo.id
          "name" -> repo.name
          "url" -> repo.url
        }
      } onSuccess {
        case res => self ! IndexComplete(res.getIndex, res.getId, res.getType, res.getVersion)
      }
    }

    case IndexArtifact(gav) => {
      log.info(s"Indexing ${gav}")
      client.execute {
        index into "artifacts" -> gav.classifier.getOrElse("binary") fields {
          "artifactId" -> gav.artifactId
          "groupId" -> gav.groupId
          "version" -> gav.version
          "classifier" -> gav.classifier
          "path" -> gav.filePath
          "url" -> gav.url
        }
      } onSuccess {
        case res => self ! IndexComplete(res.getIndex, res.getId, res.getType, res.getVersion)
      }
    }
    case IndexClass(info) => {
      client.execute {
        index into "classes" fields {
          "class" -> info.name
          "signature" -> info.signature
          "parentClass" -> info.superClassName
          "location" -> info.location
          "interfaces" -> info.interfaces
        }
      } onSuccess {
        case res => sender ! IndexComplete(res.getIndex, res.getId, res.getType, res.getVersion)
      }
    }

    case comp@IndexComplete => {
      log.error(s"Completed indexing of ${comp}")
    }

  }
}
