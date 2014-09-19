package com.achals.raft.rpc

/**
 * Created by achalshah on 9/17/14.
 */
case class ElectionVoteResponse(term: Int, voteGranted: Boolean) {
}
