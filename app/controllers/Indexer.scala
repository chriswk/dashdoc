package controllers

import play.api.mvc._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import akka.actor.Props
import actors.FolderIndexer
import model.IndexPath
import play.Configuration
import java.nio.file.Paths

object Indexer extends Controller {
  val folderIndexer = Akka.system.actorOf(Props[FolderIndexer])
  val fileFolderPath = Paths.get(Configuration.root().getString("artifact.path"))

  def indexAll = Action {
    folderIndexer ! IndexPath(fileFolderPath, fileFolderPath)
    Redirect(routes.Search.searchForClass)
  }
}
