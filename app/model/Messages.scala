package model

import org.clapper.classutil.ClassInfo
import java.nio.file.Path

case class IndexClass(classdata: ClassInfo, rootDir: Path)
case class IndexComplete(index: String, id: String, indexType: String, version: Long)
case class IndexArtifact(gav: GAV)
case class IndexRepo(repo: Repo)
case class IndexPath(path: Path, rootDir: Path)
case class WatchFolder(path: Path)
case class CreateClassIndex()

