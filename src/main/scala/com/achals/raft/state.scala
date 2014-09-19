package com.achals.raft

/**
 * Created by achalshah on 9/18/14.
 */
object State extends Enumeration {
  type State = Value
  val Follower, Candidate, Leader = Value
}
