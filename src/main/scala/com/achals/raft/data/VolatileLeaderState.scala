package com.achals.raft.data

/**
 * Created by achalshah on 9/16/14.
 */
case class VolatileLeaderState(nextIndex: Map[CandidateId, Int], matchIndex: Map[CandidateId, Int]) {

}
