package com.study.unit.stateless

import org.scalatest.{FlatSpec, Matchers}

class IncrementMapFunctionTest extends FlatSpec with Matchers {
  "IncrementMapFunction" should "increment values" in {
    // instantiate your function
    val incrementer: IncrementMapFunction = new IncrementMapFunction()

    // call the methods that you have implemented
    incrementer.map(2) should be (3)
  }
}
