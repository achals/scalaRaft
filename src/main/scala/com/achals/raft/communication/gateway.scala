package com.achals.raft.communication

import com.achals.raft.data.ClientId
import com.achals.raft.rpc._

/**
 * Created by achalshah on 9/18/14.
 */
trait Gateway {
  def appendEntries(clientId: ClientId, request: AppendEntriesRequest): AppendEntriesResponse
  def requestVote(clientId: ClientId, request: ElectionVoteRequest): ElectionVoteResponse
}
