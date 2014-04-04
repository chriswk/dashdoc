package model
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
