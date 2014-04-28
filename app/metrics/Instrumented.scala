package metrics

import com.kenshoo.play.metrics.MetricsRegistry
import nl.grons.metrics.scala.InstrumentedBuilder

trait Instrumented extends InstrumentedBuilder {
  val metricRegistry = MetricsRegistry.default

}
