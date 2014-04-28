package metrics

import akka.actor.{ActorLogging, Actor}
import nl.grons.metrics.scala._


trait ReceiveCounterActor extends Actor { self: InstrumentedBuilder =>

  def receiveCounterName: String = MetricName(getClass).append("receiveCounter").name
  lazy val counter: Counter = metrics.counter(receiveCounterName)

  private[this] lazy val wrapped = counter.count(super.receive)

  abstract override def receive = wrapped

}

trait ReceiveTimerActor extends Actor with ActorLogging with ReceiveCounterActor with ReceiveExceptionMeterActor with Instrumented { self: InstrumentedBuilder =>

  def receiveTimerName: String = MetricName(getClass).append("receiveTimer").name
  lazy val timer: Timer = metrics.timer(receiveTimerName)

  private[this] lazy val wrapped = timer.timePF(super.receive)

  abstract override def receive = wrapped
}

trait ReceiveExceptionMeterActor extends Actor { self: InstrumentedBuilder =>

  def receiveExceptionMeterName: String = MetricName(getClass).append("receiveExceptionMeter").name
  lazy val meter: Meter = metrics.meter(receiveExceptionMeterName)

  private[this] lazy val wrapped = meter.exceptionMarkerPF(super.receive)

  abstract override def receive = wrapped

}
