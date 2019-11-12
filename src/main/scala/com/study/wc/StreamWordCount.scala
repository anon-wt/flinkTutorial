package com.study.wc

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object StreamWordCount {
  def main(args: Array[String]): Unit = {
    // stream 环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
//    env.disableOperatorChaining() 针对比较的的task ，可以选择不进行操作链合并
    // 传入参数
    val paramTool: ParameterTool = ParameterTool.fromArgs(args)

//    val host = "localhost"
//    val port = 7777
    val host = paramTool.get("host")
    val port = paramTool.getInt("port")

    // 接受一个socket文本流
    val streamData = env.socketTextStream(host, port)

    // 数据流处理
    val wordCount = streamData.flatMap(_.split(" "))
      .filter(_.nonEmpty)
//      .disableChaining() 针对算子不使用chain合并操作
//      .startNewChain() 强行断开形成一个新链
      .map((_, 1))
      .keyBy(0)  // 流式没有group by 只有keyby 代替 nc -lk 7777
      .sum(1)

    wordCount.print()
//      .setParallelism(2) //设置并行度 及 slot 的个数 默认线是核数 cpu的core 数
    // 任务执行
    env.execute("stream wordcount job")
  }

}
