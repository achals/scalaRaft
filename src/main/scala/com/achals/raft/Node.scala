package com.achals.raft

import com.achals.raft.State.State
import com.achals.raft.communication.AkkaGateway
import com.achals.raft.dao.PersistentStateDao
import com.achals.raft.data.{LogEntry, PersistentState, ClientId}
import com.achals.raft.rpc.{ElectionVoteResponse, ElectionVoteRequest}

import org.joda.time.Seconds

import org.slf4j.{LoggerFactory, Logger}

import scala.util.Random

/**
 * Created by achalshah on 9/16/14.
 */
class Node(val clientId: ClientId,
           val stateDao: PersistentStateDao) {

  val LOG = LoggerFactory.getLogger( "Node" )

  val clientGateway = new AkkaGateway(this, Seconds.seconds(1));
  clientGateway.scheduleNewTimer()
  var state: State = State.Follower
  var servers: Set[ClientId] = Set()

  var commitIndex: Int = 0
  var lastApplied: LogEntry = null

  def contestForLeader() = {
    LOG.info("{} contesting for election.", this.clientId)

    this.state = State.Candidate
    val currentState = this.stateDao.getLatestState()
    val newState = this.stateDao.updateState(PersistentState(currentState.currentTerm + 1,
                                                             this.clientId,
                                                             currentState.log))


    this.voteForSelf()
    this.resetTimer()
    servers.foreach(this.requestVotesFromServer)
  }

  // Leader operations.
  def appendEntries() = {}

  def put(key:String, value:String) = {}

  def voteReceived(clientId: ClientId) = {}

  def voteForSelf() = {
    this.voteReceived(this.clientId)
  }

  def voteFor( clientId : ClientId, state: PersistentState ) = {
  }

  def resetTimer() = {
    this.clientGateway.scheduleNewTimer()
  }

  def requestVotesFromServer( server: ClientId ) = {
    val latestState = this.stateDao.getLatestState()
    val request = ElectionVoteRequest( latestState.currentTerm,
                                       latestState.votedFor,
                                       this.commitIndex,
                                       this.lastApplied.term )
    this.clientGateway.requestVote(server, request)
  }

  def respondToVoteRequest( clientId: ClientId, request: ElectionVoteRequest) = {
    LOG.info("{} got request {} from {}.", this.clientId, request, clientId)
  }

  def respondToVoteResponse( clientId: ClientId, response: ElectionVoteResponse) = {
    LOG.info("{} got response {} from {}.", this.clientId, response, clientId)
  }
}
