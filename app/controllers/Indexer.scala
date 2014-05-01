package controllers

import play.api.mvc._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import akka.actor.Props
import actors.{ElasticIndexer, FolderIndexer}
import model.{CreateClassIndex, IndexPath}
import play.Configuration
import java.nio.file.Paths

object Indexer extends Controller {
  val folderIndexer = Akka.system.actorOf(Props[FolderIndexer])
  val fileFolderPath = Paths.get(Configuration.root().getString("artifact.path"))
  val elasticIndexer = Akka.system.actorOf(Props[ElasticIndexer])
  def indexAll = Action {
    elasticIndexer ! CreateClassIndex()
    folderIndexer ! IndexPath(fileFolderPath, fileFolderPath)
    Redirect(routes.Search.searchForClass)
  }
}
