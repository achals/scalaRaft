package com.achals.raft

import com.achals.raft.dao.PersistentStateDao

/**
 * Created by achalshah on 9/16/14.
 */
abstract class Node {
  val stateDao: PersistentStateDao
}
