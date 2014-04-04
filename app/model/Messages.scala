package model

import org.clapper.classutil.ClassInfo

case class IndexClass(classdata: ClassInfo)
case class IndexComplete(index: String, id: String, indexType: String, version: Long)
case class IndexArtifact(gav: GAV)
case class IndexRepo(repo: Repo)
case class IndexFolder(folderPath: String)

