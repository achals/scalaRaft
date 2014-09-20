package com.achals.raft.communication

import akka.actor.Actor

/**
 * Created by achalshah on 9/19/14.
 */
class CommunicatingActor extends Actor {
  def receive = {
    case x => println(x)
  }
}
