package com.achals.raft

import com.achals.raft.State.State
import com.achals.raft.communication.AkkaGateway
import com.achals.raft.dao.PersistentStateDao
import com.achals.raft.data.{LogEntry, PersistentState, ClientId}
import com.achals.raft.rpc.{ElectionVoteResponse, ElectionVoteRequest}

import org.joda.time.Seconds

import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
 * Created by achalshah on 9/16/14.
 */
class Node(val stateDao: PersistentStateDao) {

  val LOG = LoggerFactory.getLogger( "Node" )

  val clientGateway = new AkkaGateway(this, Seconds.seconds(1));
  val clientId = this.clientGateway.clientId
  clientGateway.scheduleNewTimer()

  var state: State = State.Follower
  val servers = mutable.HashSet[ClientId]()

  var commitIndex: Int = 0
  var lastApplied: Int = 0


  // Leader Election State
  var serversWhoHaveVotedForMe = mutable.HashSet[ClientId]()

  def contestForLeader() = {
    LOG.info("{} contesting for election.", this.clientId)

    this.state = State.Candidate
    val currentState = this.stateDao.getLatestState()
    val newState = this.stateDao.updateState(PersistentState(currentState.currentTerm + 1,
                                                             this.clientId,
                                                             currentState.log))


    this.voteForSelf()
    this.resetTimer()
    this.servers.foreach(this.requestVotesFromServer)
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

  def eraseLeaderElectionState() = {
    this.serversWhoHaveVotedForMe = mutable.HashSet[ClientId]()
  }
  def resetTimer() = {
    this.clientGateway.scheduleNewTimer()
  }

  def requestVotesFromServer( server: ClientId  ) = {
    val latestState = this.stateDao.getLatestState()
    val request = ElectionVoteRequest( latestState.currentTerm,
                                       latestState.votedFor,
                                       this.commitIndex,
                                       latestState.log.last.term)
    this.clientGateway.requestVote(server, request)
  }

  def respondToVoteRequest( clientId: ClientId, request: ElectionVoteRequest) = {
    LOG.info("{} got request {} from {}.", this.clientId, request, clientId)

    val currentTerm = this.stateDao.getLatestState().currentTerm
    if ( currentTerm < request.term ) {
      this.clientGateway.vote(clientId, ElectionVoteResponse(currentTerm, true))
    } else {
      this.clientGateway.vote(clientId, ElectionVoteResponse(currentTerm, false))
    }

  }

  def respondToVoteResponse( clientId: ClientId, response: ElectionVoteResponse) = {
    LOG.info("{} got response {} from {}.", this.clientId, response, clientId)
    if ( response.voteGranted )
    {
      this.serversWhoHaveVotedForMe += clientId
      if ( this.serversWhoHaveVotedForMe.size > (this.servers.size/2) )
      {
        this.state = State.Leader
        LOG.info("{} is now the leader.", this.clientId)
        //TODO: HEARTBEAT
      }
    }
  }
}
