package com.achals.raft

import com.achals.raft.dao.memory.InMemoryStateDao
import com.achals.raft.data.{LogEntry, PersistentState}

/**
 * Created by achalshah on 9/20/14.
 */
object App {
    def main (args: Array[String]) {
        val node1 = new Node (this.initializeNewDao ())
        val node2 = new Node (this.initializeNewDao ())
        node2.servers.+= (node1.clientId)
    }

    def initializeNewDao (): InMemoryStateDao = {
        val dao = new InMemoryStateDao ()
        val logEntry = LogEntry (null, 0)
        dao.updateState (PersistentState (0, null, List (logEntry)))
        dao
    }
}
