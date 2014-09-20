package com.achals.raft

import com.achals.raft.State.State
import com.achals.raft.communication.gateway
import com.achals.raft.dao.PersistentStateDao
import com.achals.raft.data.ClientId

import scala.util.Random

/**
 * Created by achalshah on 9/16/14.
 */
abstract class Node(stateDao: PersistentStateDao,
                    comm: gateway) {

  val clientId: ClientId = ClientId( new Random().nextString(10) )

  var initialState: State = State.Follower
  var servers: Set[ClientId]

  def contestForLeader()

  // Leader operations.
  def appendEntries()

  def put(key:String, value:String)
}
