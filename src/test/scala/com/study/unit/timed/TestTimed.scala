package com.study.unit.timed

import org.apache.flink.api.common.typeinfo.Types
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists
import org.apache.flink.streaming.api.operators.KeyedProcessOperator
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord
import org.apache.flink.streaming.util.KeyedOneInputStreamOperatorTestHarness
import org.junit.Assert
import org.scalatest.FunSuite

class TestTimed extends FunSuite {

  test("Timed Operators ProcessElement") {
    val myProcessFunction = new MyProcessFunction
    val testHarness = new KeyedOneInputStreamOperatorTestHarness[String, String, String](
      new KeyedProcessOperator[String, String, String](myProcessFunction),
      (x: String) => "1",
      Types.STRING)

    // Function time is initialized to 0
    testHarness.open()
    testHarness.processElement("world", 10)

    Assert.assertEquals(
      Lists.newArrayList(
        new StreamRecord[String]("hello world", 10)),
      testHarness.extractOutputStreamRecords)
  }

  test("Timed Operators onTimer") {
    val myProcessFunction = new MyProcessFunction
    val testHarness = new KeyedOneInputStreamOperatorTestHarness[String, String, String](
       new KeyedProcessOperator[String, String, String](myProcessFunction),
       (x: String) => "1",
       Types.STRING
     )

    testHarness.open()
    testHarness.processElement("world", 10)
    testHarness.setProcessingTime(50)
    println(testHarness.extractOutputStreamRecords())

    Assert.assertEquals(Lists.newArrayList(
      new StreamRecord[String]("hello world", 10),
      new StreamRecord[String]("Timer triggered at timestamp 50")
    ),
      testHarness.extractOutputStreamRecords()
    )
  }

}
