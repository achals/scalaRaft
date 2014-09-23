package com.achals.raft.communication.Akka

import com.achals.raft.data.ClientId
import com.achals.raft.rpc.{ElectionVoteResponse, ElectionVoteRequest}

/**
 * Created by achalshah on 9/21/14.
 */
object Messages {
  case class AkkaElectionVoteRequest( toClient: ClientId, request: ElectionVoteRequest )
  case class AkkaElectionVote( toClient: ClientId, response: ElectionVoteResponse )
}
