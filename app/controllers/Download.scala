package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import model.GAV
import akka.actor.Props
import actors.DocDownloader

object Download extends Controller {
  val downloadAction = Akka.system.actorOf(Props[DocDownloader])
  val downloadForm = Form(
    mapping(
      "groupId" -> text,
      "artifactId" -> text,
      "version" -> text,
      "classifier" -> optional(text)
    )(GAV.apply)(GAV.unapply)
  )

  def index = Action {
    Ok(views.html.downloadFile(downloadForm))
  }

  def download = Action { implicit request =>
    val gavToDownload = downloadForm.bindFromRequest().get
    downloadAction ! gavToDownload
    Redirect(routes.Search.searchForClass)
  }

}
