package com.study.unit.stateless

import org.apache.flink.api.common.functions.MapFunction

class IncrementMapFunction extends MapFunction[Long, Long]{
  override def map(t: Long): Long = t + 1
}
