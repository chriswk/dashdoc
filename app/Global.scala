import actors.ElasticIndexer
import akka.actor.Props
import com.kenshoo.play.metrics.MetricsFilter
import model.CreateClassIndex
import play.api.libs.concurrent.Execution.Implicits._
import com.sksamuel.elastic4s.ElasticClient
import org.elasticsearch.common.settings.ImmutableSettings
import play.api._
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.mvc.WithFilters
import play.Configuration

/**
 * Created by chriswk on 28/04/14.
 */
object Global extends WithFilters(MetricsFilter)
