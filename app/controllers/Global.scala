package controllers

import com.kenshoo.play.metrics.MetricsFilter
import play.api.mvc.WithFilters

/**
 * Created by chriswk on 28/04/14.
 */
object Global extends WithFilters(MetricsFilter){

}
