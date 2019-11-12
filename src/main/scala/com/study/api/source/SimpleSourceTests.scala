package com.study.api.source

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._

case class SensorReading(id: String, timestamp: Long, temperature: Double)

object SimpleSourceTests {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    // 1. list
//    val stream1 = env.fromCollection(List(
//      SensorReading("1", 12345678910123L, 2.3),
//      SensorReading("2", 12345678910145L, 4.8),
//      SensorReading("3", 12345678910153L, 5.3),
//    ))
//    stream1.print("test").setParallelism(4) // print("name")

    // 2. file
//    val stream2 = env.readTextFile("D:\\project\\study\\flinkTutorial\\src\\main\\resources\\sensorRead")
//    stream2.print("stream2")

    // 3. elem
    env.fromElements("222", 234, 1).print()

    env.execute()
  }
}
