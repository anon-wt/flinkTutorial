package com.study.unit.stateful

import java.util

import org.apache.flink.api.common.functions.util.ListCollector
import org.apache.flink.api.common.state.ValueStateDescriptor
import org.apache.flink.api.common.typeinfo.Types
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists
import org.apache.flink.streaming.api.operators.StreamFlatMap
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord
import org.apache.flink.streaming.util.KeyedOneInputStreamOperatorTestHarness
import org.junit.Assert
import org.scalatest.FunSuite

class TestStateful extends FunSuite {

  test("Stateful Operators - flatMap") {
    val mystatefulFlatMap= new MystatefulFlatMap()

    // 1. keyBy
    // OneInputStreamOperatorTestHarness takes the input and output types as type parameters
    // KeyedOneInputStreamOperatorTestHarness takes three arguments:
    // Flink operator object, key selector and key type
    val testHarness: KeyedOneInputStreamOperatorTestHarness[String, String, String] =
   new KeyedOneInputStreamOperatorTestHarness[String, String, String](
//     new StreamFlatMap(mystatefulFlatMap), new MyStringKeySelector() , Types.STRING)
     new StreamFlatMap(mystatefulFlatMap), (x: String) => "1", Types.STRING) // 匿名函数， 同上
    testHarness.open();


    // 输入第一个参数
    testHarness.processElement("world", 10)
    val previousInput = mystatefulFlatMap.getRuntimeContext.getState(
      new ValueStateDescriptor[String]("previousInput", Types.STRING))
    val stateValue = previousInput.value
    val out = testHarness.extractOutputStreamRecords

    Assert.assertEquals("world", stateValue)
    Assert.assertEquals(Lists.newArrayList(
      new StreamRecord[String]("hello world", 10)), out)


    // 输入第二个参数
    testHarness.processElement("parallel", 20)
    val stateValue2 = previousInput.value
    val out2 = testHarness.extractOutputStreamRecords

    Assert.assertEquals("parallel", stateValue2)
    Assert.assertEquals(
      Lists.newArrayList(
      new StreamRecord[String]("hello world", 10),
      new StreamRecord[String]("hello parallel world", 20)
      ), out2)
  }
}
