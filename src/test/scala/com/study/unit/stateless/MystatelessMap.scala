package com.study.unit.stateless

import org.apache.flink.api.common.functions.MapFunction

class MystatelessMap extends MapFunction[String, String]{
  override def map(in: String): String = {
   "hello " + in
  }
}
