package com.study.unit.timed

import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.util.Collector

class MyProcessFunction extends KeyedProcessFunction[String, String, String]{
  override def processElement(
    in: String,
    context: KeyedProcessFunction[String, String, String]#Context,
    collector: Collector[String]): Unit = {
    context.timerService().registerProcessingTimeTimer(50)
    val out = "hello " + in
    collector.collect(out)
  }

  override def onTimer(
    timestamp: Long,
    ctx: KeyedProcessFunction[String, String, String]#OnTimerContext,
    out: Collector[String]): Unit = {
    out.collect(s"Timer triggered at timestamp $timestamp")
  }
}
