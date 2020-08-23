package org.apache.spark.metrics.source

import com.codahale.metrics.{Gauge, MetricRegistry}
import org.apache.spark.sql.streaming.StreamingQueryProgress

object StreamingSource {


}

class StreamingSource(val appName: String) extends Source {
  register("batchId", () => _progress.batchId)
  register("numInputRows", () => _progress.numInputRows)
  register("inputRowsPerSecond", () => _progress.inputRowsPerSecond)
  register("processedRowsPerSecond", () => _progress.processedRowsPerSecond)
  final val _metricRegistry = new MetricRegistry
  final val _progress = new Progress

  private def register[T](name: String, metric: Gauge[T]) = _metricRegistry.register(MetricRegistry.name(name), metric)

  override def sourceName: String = String.format("%s.streaming", appName)

  override def metricRegistry: MetricRegistry = _metricRegistry

  def updateProgress(queryProgress: StreamingQueryProgress): Unit = {
    _progress.batchId(queryProgress.batchId)
    _progress.numInputRows(queryProgress.numInputRows)
    _progress.inputRowsPerSecond(queryProgress.inputRowsPerSecond)
    _progress.processedRowsPerSecond(queryProgress.processedRowsPerSecond)
  }

  class Progress {
    private var _batchId: Long = -1
    private var _numInputRows: Long = 0
    private var _inputRowsPerSecond: Double = 0
    private var _processedRowsPerSecond: Double = 0


    def batchId: Long = _batchId

    def batchId(batchId: Long): Unit = {
      _batchId = batchId
    }

    def numInputRows: Long = _numInputRows

    def numInputRows(numInputRows: Long): Unit = {
      _numInputRows = numInputRows
    }

    def inputRowsPerSecond: Double = _inputRowsPerSecond

    def inputRowsPerSecond(inputRowsPerSecond: Double): Unit = {
      _inputRowsPerSecond = inputRowsPerSecond
    }

    def processedRowsPerSecond: Double = _processedRowsPerSecond

    def processedRowsPerSecond(processedRowsPerSecond: Double): Unit = {
      _processedRowsPerSecond = processedRowsPerSecond
    }
  }
}
