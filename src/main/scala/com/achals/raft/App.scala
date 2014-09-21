package com.achals.raft

import java.util.ArrayList

import com.achals.raft.dao.memory.InMemoryStateDao
import com.achals.raft.data.{LogEntry, PersistentState, ClientId}

/**
 * Created by achalshah on 9/20/14.
 */
object App {
  def main (args: Array[String]) {
    val node1 = new Node( ClientId("Rish"), this.initializeNewDao() )
    val node2 = new Node( ClientId("Ach"),  this.initializeNewDao() )
  }

  def initializeNewDao(): InMemoryStateDao = {
    val dao = new InMemoryStateDao()
    dao.updateState(PersistentState(0, null, List()))
    dao
  }
}
