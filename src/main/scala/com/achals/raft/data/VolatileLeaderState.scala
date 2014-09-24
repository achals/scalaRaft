package com.achals.raft.data

/**
 * Created by achalshah on 9/16/14.
 */
case class VolatileLeaderState (nextIndex: Map[ClientId, Int], matchIndex: Map[ClientId, Int]) {

}
