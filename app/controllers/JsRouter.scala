package controllers

import play.api.mvc._
import play.api.Routes

object JsRouter extends Controller {
  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        Search.searchForClassName
      )
    ).as("text/javascript")
  }

}
