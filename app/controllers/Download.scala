package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import model.GAV
import akka.actor.Props
import actors.Downloader

object Download extends Controller {
  val downloadAction = Akka.system.actorOf(Props[Downloader])
  val downloadForm = Form(
    mapping(
      "groupId" -> text,
      "artifactId" -> text,
      "version" -> text,
      "classifier" -> optional(text)
    )(GAV.apply)(GAV.unapply)
  )

  def index = Action { implicit request =>
    Ok(views.html.downloadFile(downloadForm))
  }

  def download = Action { implicit request =>
    val gavToDownload = downloadForm.bindFromRequest().get
    val sources = gavToDownload.copy(classifier = Some("sources"))
    val javadoc = gavToDownload.copy(classifier = Some("javadoc"))
    downloadAction ! gavToDownload
    downloadAction ! sources
    downloadAction ! javadoc
    Redirect(routes.Search.searchForClass)
  }

}
