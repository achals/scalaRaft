package com.achals.raft.communication

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, Props, ActorSystem}
import com.achals.raft.Node
import org.joda.time.Seconds

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration

/**
 * Created by achalshah on 9/19/14.
 */
class AkkaGateway(clientNode: Node, electionTimeout: Seconds){
  val system: ActorSystem = ActorSystem(clientNode.clientId.id)

  val actor: ActorRef = system.actorOf(Props[CommunicatingActor])
  actor ! clientNode
  system.scheduler.schedule(new FiniteDuration(electionTimeout.getSeconds, TimeUnit.SECONDS),
                            new FiniteDuration(electionTimeout.getSeconds, TimeUnit.SECONDS),
                            actor, "TimeOut")
}
