package com.achals.raft.communication

import akka.actor.Actor

import com.achals.raft.Node

/**
 * Created by achalshah on 9/19/14.
 */
class CommunicatingActor extends Actor {

  var clientNode: Node = null

  def receive = {
    case clientNode: Node => {this.clientNode = clientNode}

    case "TimeOut" => this.clientNode.contestForLeader()

    case x => println(x)
  }
}
