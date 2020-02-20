package com.study.unit.stateful

import org.apache.flink.api.java.functions.KeySelector

class MyStringKeySelector extends KeySelector[String, String] {
  override def getKey(in: String): String = "1"
}
