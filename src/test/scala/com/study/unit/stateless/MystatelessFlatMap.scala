package com.study.unit.stateless

import org.apache.flink.api.common.functions.FlatMapFunction
import org.apache.flink.util.Collector

class MystatelessFlatMap extends FlatMapFunction[String, String]{
  override def flatMap(in: String, collector: Collector[String]): Unit = {
     val out =  "hello " + in
    collector.collect(out)
  }
}
