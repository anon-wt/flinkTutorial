package com.study.unit.stateless

import java.util

import org.apache.flink.api.common.functions.util.ListCollector
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists
import org.junit.Assert
import org.scalatest.FunSuite

class TestStateLess extends FunSuite {

  test("Stateless Operators - map") {
    val mystatelessMap = new MystatelessMap()
    val out = mystatelessMap.map("world")
    assert(out == "hello world")
  }

  test("Stateless Operators - flatMap") {
    val mystatelessFlatMap = new MystatelessFlatMap()
    val out = new util.ArrayList[String]()
    val collecter: ListCollector[String] = new ListCollector[String](out)
    mystatelessFlatMap.flatMap("world", collecter)
    Assert.assertEquals(Lists.newArrayList[String]("hello world"), out)
  }
}
