package model

import java.nio.file.{FileSystems, FileSystem}

object ModelImplicits {
  val config = play.Configuration.root()
  val repoFolder = FileSystems.getDefault.getPath(config.getString("artifact.path"))
  import java.nio.file.Path

  implicit def Path2Gav(path: Path) = for {
      versionPath: Path <- path.getParent
      artifactPath: Path <- versionPath.getParent
      groupPath: Path <- artifactPath.relativize(repoFolder)
      classifier: Option[String] <- {
        if (path.getFileName.endsWith("javadoc.jar")) {
          Some("javadoc")
        } else if (path.getFileName.endsWith("sources.jar")) {
          Some("sources")
        } else {
          None
        }
      }
  } yield GAV(groupPath.toString, artifactPath.getName(0).toString, versionPath.getName(0).toString, classifier)
}
case class GAV(groupId: String, artifactId: String, version: String, classifier: Option[String]) {
  lazy val url = {
    dotToSlash(groupId) + "/" + dotToSlash(artifactId) + "/" + version
  }
  override def toString = {
    val b = s"${groupId}:${artifactId}:${version}"
    val p = classifier match {
      case Some(cl) => s"@${cl}"
      case None => ""
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
case class ClassData(packageName: String, name: String)
case class Repo(id: String, name: String, url: String)
