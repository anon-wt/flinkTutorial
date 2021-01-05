/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.study.job.spendreport

import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.common.typeinfo.Types
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.util.Collector
import org.apache.flink.walkthrough.common.entity.{Alert, Transaction}


object FraudDetectorV2 {
  val SMALL_AMOUNT: Double = 1.00
  val LARGE_AMOUNT: Double = 500.00
  val ONE_MINUTE: Long     = 60 * 1000L
}

@SerialVersionUID(1L)
class FraudDetectorV2 extends KeyedProcessFunction[Long, Transaction, Alert] {
  // 设置 状态
  @transient private var flagState: ValueState[java.lang.Boolean] = _

  @throws[Exception]
  def processElement(
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

      flagState.clear()
    }

    // 3. 当值小于小于最小值时, 更新状态

    if (transaction.getAmount < FraudDetectorV2.SMALL_AMOUNT) {
      flagState.update(true)
    }
  }

  override def open(parameters: Configuration): Unit = {
    // 1. 初始化状态
    val flagDescriptor = new ValueStateDescriptor("flag", Types.BOOLEAN)
    flagState = getRuntimeContext.getState(flagDescriptor)
  }
}
