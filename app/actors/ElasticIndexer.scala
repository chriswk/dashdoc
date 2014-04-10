package actors

import play.api.libs.concurrent.Execution.Implicits._
import akka.actor.{Actor, ActorLogging}
import com.sksamuel.elastic4s.{KeywordAnalyzer, ElasticClient}
import com.sksamuel.elastic4s.ElasticDsl._
import model._
import play.Configuration
import org.elasticsearch.common.settings.ImmutableSettings
import com.sksamuel.elastic4s.mapping.FieldType.StringType

class ElasticIndexer extends Actor with ActorLogging {
  val c = Configuration.root()
  val client = {
    val cluster = c.getString("elasticsearch.cluster")
    val url = c.getString("elasticsearch.url")
    val port = c.getInt("elasticsearch.port")
    val settings = ImmutableSettings.builder().put("cluster.name", cluster).build()
    log.info(s"Connecting to ${url}:${port}, cluster: ${cluster}")
    ElasticClient.remote(settings, (url, port.toInt))
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
    case IndexClass(info, rootDir) => {
      client.execute {
        index into "classes" -> "class" fields (
          "className" -> info.name.substring(info.name.lastIndexOf(".")+1),
          "absolute" -> info.name,
          "signature" -> info.signature,
          "parentClass" -> info.superClassName,
          "location" -> info.location,
          "gav" -> ModelHelper.path2Gav(info.location.toPath, rootDir),
          "interfaces" -> info.interfaces,
          "methods" -> info.methods.toList
        )
      } onSuccess {
        case res => sender ! IndexComplete(res.getIndex, res.getId, res.getType, res.getVersion)
      }
    }

    case CreateClassIndex() => {
      client.execute {
        create index "classes" mappings (
          "class" as(
            "absolute" typed StringType analyzer KeywordAnalyzer,
            "signature" typed StringType analyzer KeywordAnalyzer,
            "gav" typed StringType analyzer KeywordAnalyzer
            )
          )
      }
    }

    case comp@IndexComplete => {
      log.info(s"Completed indexing of ${comp}")
    }

  }
}
