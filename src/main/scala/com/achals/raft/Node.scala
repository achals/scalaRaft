package com.achals.raft

import com.achals.raft.State.State
import com.achals.raft.dao.PersistentStateDao
import com.achals.raft.data.ClientId

/**
 * Created by achalshah on 9/16/14.
 */
class Node(stateDao: PersistentStateDao,
           initialState: State) {
  var servers: Set[ClientId]

  
}
