package com.achals.raft.dao.memory

import com.achals.raft.dao.PersistentStateDao
import com.achals.raft.data.PersistentState

/**
 * Created by achalshah on 9/20/14.
 */
class InMemoryStateDao extends PersistentStateDao{

  var persistentState: PersistentState = null;

  @Override
  def getLatestState(): PersistentState = {this.persistentState}

  @Override
  def updateState(newState: PersistentState): PersistentState = {
    this.persistentState = newState
    this.getLatestState()
  }
}
