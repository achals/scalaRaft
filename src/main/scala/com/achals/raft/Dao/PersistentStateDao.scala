package com.achals.raft.Dao

import com.achals.raft.data.PersistentState

/**
 * Created by achalshah on 9/16/14.
 */
trait PersistentStateDao {
  def getLatestState(): PersistentState
  def updateState(newState: PersistentState): PersistentState
}
