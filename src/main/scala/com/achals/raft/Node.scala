package com.achals.raft

import com.achals.raft.State.State
import com.achals.raft.communication.AkkaGateway
import com.achals.raft.dao.PersistentStateDao
import com.achals.raft.data.{LogEntry, Command, ClientId, PersistentState}
import com.achals.raft.rpc.{AppendEntriesResponse, AppendEntriesRequest, ElectionVoteRequest, ElectionVoteResponse}
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
 * Created by achalshah on 9/16/14.
 */
class Node (val stateDao: PersistentStateDao) {

    val LOG = LoggerFactory.getLogger ("Node")

    val clientGateway = new AkkaGateway (this);
    val clientId = this.clientGateway.clientId
    clientGateway.scheduleNewElectionTimer ()
    val servers = mutable.HashSet[ClientId]()
    var state: State = State.Follower
    var commitIndex: Int = 0
    var lastApplied: Int = 0

    //Follower state
    var receivedRPC = false

    // Leader Election State
    var serversWhoHaveVotedForMe = mutable.HashSet[ClientId]()

    def contestForLeader () = {
        if (this.receivedRPC) {
            this.receivedRPC = false
        }
        else {
            LOG.info ("{} contesting for election.", this.clientId)

            this.state = State.Candidate
            val currentState = this.stateDao.getLatestState ()
            val newState = this.stateDao.updateState (PersistentState (currentState.currentTerm + 1,
                this.clientId,
                currentState.log))


            this.voteForSelf ()
            this.resetElectionTimer ()
            this.servers.foreach (this.requestVotesFromServer)
        }
    }

    def voteForSelf () = {
        this.voteReceived (this.clientId)
    }

    def voteReceived (clientId: ClientId) = {}

    def resetElectionTimer () = {
        this.clientGateway.scheduleNewElectionTimer ()
    }

    def requestVotesFromServer (server: ClientId) = {
        val latestState = this.stateDao.getLatestState ()
        val request = ElectionVoteRequest (latestState.currentTerm,
            latestState.votedFor,
            this.commitIndex,
            latestState.log.last.term)
        this.clientGateway.requestVote (server, request)
    }

    // Leader operations.
    def appendEntry ( command: Command ) = {
        for( server <- this.servers )(this.sendEntryToClient(server, command))
    }

    def sendHeartbeatToAllServers () = {
        this.servers.foreach (this.sendHeartbeat)
    }

    def sendEntryToClient(clientId: ClientId, command: Command) = {
        val term = this.stateDao.getLatestState ().currentTerm
        val request = AppendEntriesRequest (term,
            this.clientId,
            this.stateDao.getLatestState ().log.size,
            this.stateDao.getLatestState ().log.last.term,
            List (LogEntry(command, term)),
            this.commitIndex)

        this.clientGateway.appendEntries(clientId, request)
    }

    def sendHeartbeat (clientId: ClientId) = {
        val request = AppendEntriesRequest (this.stateDao.getLatestState ().currentTerm,
            this.clientId,
            this.stateDao.getLatestState ().log.size,
            this.stateDao.getLatestState ().log.last.term,
            List (),
            this.commitIndex)
        this.clientGateway.appendEntries (clientId, request)
    }

    def eraseLeaderElectionState () = {
        this.serversWhoHaveVotedForMe = mutable.HashSet[ClientId]()
    }

    def respondToVoteRequest (clientId: ClientId, request: ElectionVoteRequest) = {
        LOG.info ("{} got request {} from {}.", this.clientId, request, clientId)
        val currentTerm = this.stateDao.getLatestState ().currentTerm

        LOG.debug ("Current term is {}, term in vote is {}.", currentTerm, request.term)
        if (currentTerm < request.term) {
            this.updateCurrentTerm (request.term)
            this.updateRecievedRPC ()
            this.clientGateway.vote (clientId, ElectionVoteResponse (currentTerm, true))
        } else {
            this.clientGateway.vote (clientId, ElectionVoteResponse (currentTerm, false))
        }

    }

    def updateRecievedRPC () = {
        this.receivedRPC = true
    }

    def updateCurrentTerm (newTerm: Int) = {
        val currentState = this.stateDao.getLatestState ()
        if (currentState.currentTerm < newTerm) {
            this.stateDao.updateState (PersistentState (newTerm, currentState.votedFor, currentState.log))
            this.state = State.Follower
        }
    }

    def respondToVoteResponse (clientId: ClientId, response: ElectionVoteResponse) = {
        LOG.info ("{} got response {} from {}.", this.clientId, response, clientId)
        this.updateCurrentTerm (response.term)
        if (response.voteGranted) {
            this.serversWhoHaveVotedForMe += clientId
            if (this.serversWhoHaveVotedForMe.size > (this.servers.size / 2)) {
                this.state = State.Leader
                LOG.info ("{} is now the leader.", this.clientId)
                this.resetHeartbeatTimer ()
            }
        }
        else {
            this.state = State.Follower
        }
    }

    def resetHeartbeatTimer () = {
        this.clientGateway.scheduleNewHeartbeatTimer ()
    }

    def respondToAppendEntriesRequest (clientId: ClientId, request: AppendEntriesRequest): Unit = {
        LOG.info ("Got heart beat from {}.", clientId)
        this.resetElectionTimer ()

        if ( request.entries.isEmpty )
        {
            return
        }

        val currentState = this.stateDao.getLatestState()
        if ( request.termOfLeader < currentState.currentTerm ||  currentState.log(request.prevLogIndex).term != request.prevLogTerm) {
            this.clientGateway.respondToAppendEntriesRequest (clientId, AppendEntriesResponse(currentState.currentTerm, false))
        }
    }
}
