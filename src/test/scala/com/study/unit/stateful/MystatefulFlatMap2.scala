package com.study.unit.stateful

import org.apache.flink.api.common.functions.FlatMapFunction
import org.apache.flink.runtime.state.{FunctionInitializationContext, FunctionSnapshotContext}
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction
import org.apache.flink.util.Collector
import org.apache.flink.api.common.state.{ListState, ListStateDescriptor, ValueStateDescriptor}
import org.apache.flink.api.common.typeinfo.{TypeInfo, TypeInformation}

/**
 * no keyed operator  ???
 */
// todo
class MystatefulFlatMap2 extends FlatMapFunction[Long, Long] with CheckpointedFunction{
  private var operatorState: ListState[Long] = _
  private var beforeElement: Long = 0

  override def flatMap(t: Long, collector: Collector[Long]): Unit = {
    
    collector.collect(t + operatorState.get().iterator().next())

  }

  override def snapshotState(functionSnapshotContext: FunctionSnapshotContext): Unit = {
    operatorState.clear()
    operatorState.add(beforeElement)
  }


  override def initializeState(context: FunctionInitializationContext): Unit = {
    val OperatorDescriptor = new ListStateDescriptor[Long]("OperatorState", TypeInformation.of(classOf[Long]))
    operatorState = context.getOperatorStateStore.getListState(OperatorDescriptor)
  }
}
