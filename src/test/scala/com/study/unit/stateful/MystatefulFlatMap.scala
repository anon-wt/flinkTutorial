package com.study.unit.stateful

import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.common.typeinfo.Types
import org.apache.flink.configuration.Configuration
import org.apache.flink.util.Collector

class MystatefulFlatMap extends RichFlatMapFunction[String, String]{
  var previousInput: ValueState[String] = _

  override def open(parameters: Configuration): Unit = {
    previousInput = getRuntimeContext.getState(
      new ValueStateDescriptor[String]("previousInput", Types.STRING)
    )
  }
  override def flatMap(in: String, collector: Collector[String]): Unit = {
    var out: String = "hello " + in
    if (previousInput.value != null) {
      out = out + " " + previousInput.value
    }
    previousInput.update(in)
    collector.collect(out)
  }

}
