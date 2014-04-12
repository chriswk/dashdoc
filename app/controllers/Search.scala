package controllers

import play.Configuration
import play.api.mvc._
import org.elasticsearch.common.settings.ImmutableSettings
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.data._
import play.api.data.Forms._


object Search extends Controller {
  val c = Configuration.root()
  val client = {
    val cluster = c.getString("elasticsearch.cluster")
    val url = c.getString("elasticsearch.url")
    val port = c.getInt("elasticsearch.port")
    val settings = ImmutableSettings.builder().put("cluster.name", cluster).build()
    Logger.info(s"Connecting to ${url}:${port}, cluster: ${cluster}")
    ElasticClient.remote(settings, (url, port.toInt))
  }

  val searchForm = Form("name" -> text)



  def searchForClass = Action.async {
    client.execute {
      search in "classes" -> "class" facets (
        facet terms "Coord" field "gav",
        facet terms "File" field "location"
        )
    }.map(r => {
      Ok(r.toString)
    })
  }

  def searchForClassName = Action.async { implicit request =>
      val className = searchForm.bindFromRequest().get
      Logger.info(s"Searching for ${className}")
      client.execute {
        search in "classes" -> "class" query className
      }.map(r => Ok(r.toString))
  }

}
