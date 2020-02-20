package com.study.unit.stateful

import org.apache.flink.streaming.api.operators.StreamFlatMap
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

/**
 *
 */
// todo
class StatefulFlatMapTest extends FlatSpec with Matchers with BeforeAndAfter {
  private var testHarness: OneInputStreamOperatorTestHarness[Long, Long] = null
  private var statefulFlatMap: MystatefulFlatMap2 = null

  before {
    //instantiate user-defined function
    statefulFlatMap = new MystatefulFlatMap2

    // wrap user defined function into a the corresponding operator 
    testHarness = new OneInputStreamOperatorTestHarness[Long, Long](new StreamFlatMap(statefulFlatMap))

    // optionally configured the execution environment
    testHarness.getExecutionConfig.setAutoWatermarkInterval(50);

    // open the test harness (will also call open() on RichFunctions)
    testHarness.open();
  }

  "StatefulFlatMap" should "do some fancy stuff with timers and state" in {
    //push (timestamped) elements into the operator (and hence user defined function)
    testHarness.processElement(2, 100);

    //trigger event time timers by advancing the event time of the operator with a watermark
    testHarness.processWatermark(100);

    //trigger proccesign time timers by advancing the processing time of the operator directly
    testHarness.setProcessingTime(100);

    //retrieve list of emitted records for assertions
    println(testHarness.getOutput)
//    should contain (3)

    //retrieve list of records emitted to a specific side output for assertions (ProcessFunction only)
    //testHarness.getSideOutput(new OutputTag[Int]("invalidRecords")) should have size 0
  }
}
