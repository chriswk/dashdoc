package controllers

import play.Configuration
import play.api.mvc._
import org.elasticsearch.common.settings.ImmutableSettings
import com.sksamuel.elastic4s.{QueryDefinition, ElasticClient}
import com.sksamuel.elastic4s.ElasticDsl._
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json
import org.elasticsearch.search.{SearchHit, SearchHits}


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

  case class ClassSearch(name: String)

  val searchForm = Form(
    mapping(
      "name" -> text()
    )(ClassSearch.apply)(ClassSearch.unapply)
  )

  def searchForClass = Action {
    Ok(views.html.search(searchForm))
  }

  def searchForClassName = Action.async { implicit request =>
    val className = searchForm.bindFromRequest().get
    Logger.info(s"Searching for ${className}")
    client.execute {
      search in "classes" -> "class" query {
        prefix (
          "className" -> className.name.toLowerCase
        )
      }
    }.map(r => {
      Ok(r.toString).as("application/json")
    })
  }


  def browse = Action {
    Ok(views.html.browse("hello"))
  }

  def browseJson = Action.async { implicit request =>
    val page = request.getQueryString("sEcho").getOrElse("0").toInt
    val rowSize = request.getQueryString("iDisplayLength").getOrElse("10").toInt
    val start = page * rowSize
    client.execute {
      search in "classes" -> "class" query {
        fetchQuery(request.getQueryString("sSearch"))
      }
    }.map(r => {
      Ok(r.toString).as("application/json")
    })
  }


  def fetchQuery(queryString: Option[String]): QueryDefinition = {
    queryString match {
      case Some(queryString) => prefix(
        "className" -> queryString.toLowerCase
      )
      case None => matchall
    }
  }
}
