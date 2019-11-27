package com.study.api.source

import java.util.Properties

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer011, Kafka010TableSource}

import scala.util.Random

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

    /// 4. kafka
    // 3. 从kafka读取数据
    // 定义相关的配置
//    val properties = new Properties()
//    properties.setProperty("bootstrap.servers", "localhost:9092")
//    properties.setProperty("group.id", "consumer-group")
//    properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
//    properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
//    properties.setProperty("auto.offset.reset", "latest")
//
//    val stream3 = env.addSource(new FlinkKafkaConsumer011[String]("sensor", new SimpleStringSchema(), properties))

    // 4. 自定义source
    val stream4 = env.addSource(new MySensorSource())
    stream4.print()

    env.execute()
  }

}
class MySensorSource() extends SourceFunction[SensorReading]{
  // 定义一个标识位，表示数据源是否继续运行
  var running: Boolean = true

  override def cancel(): Unit = {
    running = false
  }

  // 随机生成自定义的传感器数据
  override def run(ctx: SourceFunction.SourceContext[SensorReading]): Unit = {
    // 初始化一个随机数发生器
    val rand = new Random()

    // 初始化10个传感器数据，随机生成
    var curTemp = 1.to(10).map(
      i => ( "sensor_" + i, 60 + rand.nextGaussian() * 20 )
    )
    // 无限循环，在初始温度值基础上随机波动，产生随机的数据流
    while( running ){
      // 对10个数据更新温度值
      curTemp = curTemp.map(
        data => ( data._1, data._2 + rand.nextGaussian() )
      )
      // 获取当前的时间戳，包装成样例类
      val curTime = System.currentTimeMillis()
      curTemp.foreach(
        data => ctx.collect( SensorReading(data._1, curTime, data._2) )
      )
      // 间隔500ms
      Thread.sleep(500)
    }
  }
}