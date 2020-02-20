package com.study.unit.job

import java.util

import com.study.unit.stateless.IncrementMapFunction
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class StreamingJobIntegrationTest extends FlatSpec with Matchers with BeforeAndAfter  {
  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)

  before {
    flinkCluster.before()
  }

  after {
    flinkCluster.after()
  }

  "IncrementFlatMapFunction pipeline" should "incrementValues" in {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // configure your test environment
    env.setParallelism(1)
    // values are collected in a static variable
    CollectSink.values.clear()
    // create a stream of custom elements and apply transformations
    env.fromElements(1L, 21L, 22L)
      .map(new IncrementMapFunction())
      .addSink(new CollectSink())

    // execute
    env.execute()

    // verify your results
    CollectSink.values should contain allOf (2, 22, 23)
  }



}
// create a testing sink
class CollectSink extends SinkFunction[Long] {

  override def invoke(value: Long): Unit = {
    synchronized {
      CollectSink.values.add(value)
    }
  }
}

object CollectSink {
  // must be static
  val values: util.ArrayList[Long] = new util.ArrayList()
}