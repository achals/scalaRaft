package com.achals.raft.rpc

import com.achals.raft.data.{LogEntry, ClientId}

/**
 * Created by achalshah on 9/17/14.
 */
case class AppendEntriesRequest(termOfLeader: Int,
                                leaderId: ClientId,
                                prevLogIndex: Int,
                                prevLogTerm: Int,
                                entries: List[LogEntry],
                                leaderCommit: Int) {
}
