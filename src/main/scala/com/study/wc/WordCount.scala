package com.study.wc

import org.apache.flink.api.scala._

// 初学flink
object WordCount {
  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
    val inputPath = "D:\\project\\study\\flinkTutorial\\src\\main\\resources\\hellowork"
    val text = env.readTextFile(inputPath)
    val result = text.flatMap(_.split(" "))
      .map((_, 1))
//      .groupBy(_._1)
      .groupBy(0) // 与上面等同 其中1 代表二元组的第一个值, sum 同理
      .sum(1)
    result.print()
  }

}
