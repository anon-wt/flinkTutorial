package com.study.job.spendreport

import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.common.typeinfo.Types
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.util.Collector
import org.apache.flink.walkthrough.common.entity.{Alert, Transaction}

object FraudDetectorV3 {
  val SMALL_AMOUNT: Double = 1.00
  val LARGE_AMOUNT: Double = 500.00
  val ONE_MINUTE: Long     = 60 * 1000L
}

@SerialVersionUID(1L)
class FraudDetectorV3 extends KeyedProcessFunction[Long, Transaction, Alert] {
  // 设置状态
  @transient private var flagState: ValueState[java.lang.Boolean] = _
  @transient private var timerState: ValueState[java.lang.Long] = _

  @throws[Exception]
  override def processElement(
    transaction: Transaction,
    context: KeyedProcessFunction[Long, Transaction, Alert]#Context,
    collector: Collector[Alert]): Unit = {
    // 1. 获得当前状态
    val lastTransactionWasSmall = flagState.value

    // 2. 当状态不为空时,说明之前出现小的数值了， 然后判断现在数值是否大于最大值， 如果大, 则告警
    if (lastTransactionWasSmall != null) {
      if (transaction.getAmount > FraudDetectorV2.LARGE_AMOUNT) {
        val alter = new Alert
        alter.setId(transaction.getAccountId)

        collector.collect(alter)
      }

      cleanUp(context)
    }

    // 3. 当值小于小于最小值时, 更新状态， 添加定时器

    if (transaction.getAmount < FraudDetectorV2.SMALL_AMOUNT) {
      flagState.update(true)

      // set the timer and timer state
      val timer = context.timerService.currentProcessingTime + FraudDetectorV3.ONE_MINUTE
      context.timerService.registerProcessingTimeTimer(timer)
      timerState.update(timer)
    }

  }

  // 目前假的
  @throws[Exception]
  override def onTimer(
     timestamp: Long,
     ctx: KeyedProcessFunction[Long, Transaction, Alert]#OnTimerContext,
     out: Collector[Alert]): Unit = {
    // remove flag after 1 minute
    timerState.clear()
    flagState.clear()
  }

  // 设置状态
  override def open(parameters: Configuration): Unit = {
    // 注册上一个数量状态
    val flagDescriptor = new ValueStateDescriptor[java.lang.Boolean]("flag-state", Types.BOOLEAN)
    flagState = getRuntimeContext.getState(flagDescriptor)

    // 注册时间状态
    val timerDescriptor = new ValueStateDescriptor[java.lang.Long]("timer-state", Types.LONG)
    timerState = getRuntimeContext.getState(timerDescriptor)
  }

  @throws[Exception]
  private def cleanUp(ctx: KeyedProcessFunction[Long, Transaction, Alert]#Context): Unit = {
    // delete timer
    val timer = timerState.value
    ctx.timerService.deleteProcessingTimeTimer(timer)

    // clean up all states
    timerState.clear()
    flagState.clear()
  }
}
