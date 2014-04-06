package model

import java.nio.file.{Paths, FileSystems, FileSystem}
import play.Configuration
import java.io.File
import java.util.regex.Matcher

object ModelHelper {
  val config = Configuration.root()
  val repoFolder = Paths.get(config.getString("artifact.path"))

  import java.nio.file.Path

  def path2Gav(path: Path) = {
    val versionPath = path.getParent
    val artifactPath = versionPath.getParent
    val groupPath = artifactPath.subpath(1, artifactPath.getNameCount - 1)
    val classifier: Option[String] = {
      val fileName = path.toFile.getName
      if (fileName.endsWith("javadoc.jar")) {
        Some("javadoc")
      } else if (fileName.endsWith("sources.jar")) {
        Some("sources")
      } else if (fileName.endsWith("tests.jar")) {
        Some("tests")
      } else {
        None
      }
    }
    GAV(pathToDot(groupPath), artifactPath.getFileName.toString, versionPath.getFileName.toString, classifier)
  }

  def pathToDot(s: Path) = s.toString.replaceAll(Matcher.quoteReplacement(File.separator), ".")
}

case class GAV(groupId: String, artifactId: String, version: String, classifier: Option[String]) {
  lazy val url = {
    dotToSlash(groupId) + "/" + artifactId + "/" + version
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
