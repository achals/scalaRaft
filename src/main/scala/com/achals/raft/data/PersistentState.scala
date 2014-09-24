package com.achals.raft.data

/**
 * Created by achalshah on 9/16/14.
 */
case class PersistentState (currentTerm: Int, votedFor: ClientId, log: List[LogEntry]) {

}
