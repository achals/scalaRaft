package com.achals.raft.communication

import com.achals.raft.rpc._

/**
 * Created by achalshah on 9/18/14.
 */
trait Gateway {
  def appendEntries(request: AppendEntriesRequest): AppendEntriesResponse
  def requestVote(request: ElectionVoteRequest): ElectionVoteResponse
}
