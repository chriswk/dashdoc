package actors

import play.api.libs.concurrent.Execution.Implicits._
import akka.actor.{Actor, ActorLogging}
import com.sksamuel.elastic4s.{KeywordAnalyzer, ElasticClient}
import com.sksamuel.elastic4s.ElasticDsl._
import model._
import play.Configuration
import org.elasticsearch.common.settings.ImmutableSettings
import com.sksamuel.elastic4s.mappings.FieldType._

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
      val gav = ModelHelper.path2Gav(info.location.toPath, rootDir)
      log.info(
        s"""Indexing ${info}
           | in ${gav}
         """.stripMargin)
      client.execute {
        index into "classes" -> "class" fields (
          "className" -> info.name.substring(info.name.lastIndexOf(".")+1),
          "absolute" -> info.name,
          "signature" -> info.signature,
          "parentClass" -> info.superClassName,
          "location" -> info.location,
          "gav" -> gav,
          "interfaces" -> info.interfaces,
          "methods" -> info.methods.toList,
          "isAbstract" -> info.isAbstract,
          "isConcrete" -> info.isConcrete,
          "isFinal" -> info.isFinal,
          "isInterface" -> info.isInterface,
          "isPrivate" -> info.isPrivate,
          "isProtected" -> info.isProtected,
          "isPublic" -> info.isPublic,
          "isStatic" -> info.isStatic,
          "isSynchronized" -> info.isSynchronized,
          "isSynthetic" -> info.isSynthetic
        ) id gav + ":" + info.name
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
