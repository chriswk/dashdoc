package actors

import akka.actor.{Actor, ActorLogging}
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._

case class GAV(groupId: String, artifactId: String, version: String, classifier: Option[String]) {
  lazy val url = {
    dotToSlash(groupId) + "/" + dotToSlash(artifactId) + "/" + version
  }
  override def toString = {
    val b = s"${groupId}:${artifactId}:${version}"
    val p = classifier match {
      case Some(cl) => s"@${cl}"
    }
    b + p
  }
  lazy val filePath = {
    val basePath = s"${artifactId}-${version}"
    val add = classifier match {
      case Some(c) => s"-${c}.jar"
      case None => ".jar"
    }

    basePath + add
  }

  lazy val downloadUrl = url + "/" + filePath

  def dotToSlash(s: String): String = s.replaceAll("\\.", "/")
}

case class Repo(id: String, name: String, url: String)

case class IndexRepo(repo: Repo)

case class CreateRepoIndex()

case class IndexClass(gav: GAV, className: String)

case class IndexArtifact(gav: GAV)

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

    case IndexArtifact(gav) => {
      client.execute {
        index into "artifacts" -> gav.toString fields {
          "artifactId" -> gav.artifactId
          "groupId" -> gav.groupId
          "version" -> gav.version
          "classifier" -> gav.classifier
          "path" -> gav.filePath
          "url" -> gav.url
        }
      }
    }
    case IndexClass(gav, className) => {
      client.execute {
        index into "classes" -> gav.toString fields {
          "class" -> className
        }
      }
    }

  }
}
