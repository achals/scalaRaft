package com.achals.raft

import com.achals.raft.dao.memory.InMemoryStateDao
import com.achals.raft.data.ClientId

/**
 * Created by achalshah on 9/20/14.
 */
object App {
  def main (args: Array[String]) {
    val node1 = new Node( ClientId("Rish"), new InMemoryStateDao() )
    val node2 = new Node( ClientId("Ach"),  new InMemoryStateDao() )
  }
}
