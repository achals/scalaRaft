package com.achals.raft.communication.Akka

import com.achals.raft.data.ClientId
import com.achals.raft.rpc.{AppendEntriesRequest, ElectionVoteRequest, ElectionVoteResponse}

/**
 * Created by achalshah on 9/21/14.
 */
object Messages {

    case class AkkaAppendEntriesRequest (toClient: ClientId, request: AppendEntriesRequest)

    case class AkkaAppendEntriesResponse( toClient: ClientId, response: AkkaAppendEntriesResponse )

    case class AkkaElectionVoteRequest (toClient: ClientId, request: ElectionVoteRequest)

    case class AkkaElectionVote (toClient: ClientId, response: ElectionVoteResponse)

}
