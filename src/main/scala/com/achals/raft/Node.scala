package com.achals.raft

import com.achals.raft.State.State
import com.achals.raft.communication.{Gateway, AkkaGateway}
import com.achals.raft.dao.PersistentStateDao
import com.achals.raft.data.ClientId
import org.joda.time.Seconds

import scala.util.Random

/**
 * Created by achalshah on 9/16/14.
 */
class Node(stateDao: PersistentStateDao) {

  val clientId: ClientId = ClientId( new Random().nextString(10) )
  val clientGateway = new AkkaGateway(this, Seconds.seconds(1));
  var initialState: State = State.Follower
  var servers: Set[ClientId] = Set()

  def contestForLeader() = {
    println("Contesting for Leader.")
  }

  // Leader operations.
  def appendEntries() = {}

  def put(key:String, value:String) = {}
}
