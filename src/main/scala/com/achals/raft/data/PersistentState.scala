package com.achals.raft.data

import com.achals.raft.data.CandidateId

/**
 * Created by achalshah on 9/16/14.
 */
case class PersistentState(currentTerm :Int , votedFor: CandidateId, log:List[LogEntry]) {

}
