package com.achals.raft.rpc

import com.achals.raft.data.ClientId

/**
 * Created by achalshah on 9/17/14.
 */
case class ElectionVoteRequest(term: Int, candidateId: ClientId, lastLogIndex: Int, lastLogTerm: Int) {
}
