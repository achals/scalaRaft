package com.achals.raft

import com.achals.raft.dao.memory.InMemoryStateDao

/**
 * Created by achalshah on 9/20/14.
 */
object App {
  def main (args: Array[String]) {
    val stateDao = new InMemoryStateDao()
    val node = new Node(stateDao)
  }
}
