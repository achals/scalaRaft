package com.achals.raft.communication

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, Props, ActorSystem}
import com.achals.raft.data.ClientId
import org.slf4j.LoggerFactory


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration

/**
 * Created by achalshah on 9/21/14.
 */
object AkkaActorSystem {

  val LOG = LoggerFactory.getLogger(this.getClass.toString)

  val system = ActorSystem("com-achals-raft")

  def getClient() = {
    system.actorOf(Props[CommunicatingActor])
  }

  def scheduleNewTimer(actor: ActorRef, electionTimeout: Int) = {
    Option(system.scheduler.schedule(new FiniteDuration(electionTimeout, TimeUnit.MILLISECONDS),
                                     new FiniteDuration(electionTimeout, TimeUnit.MILLISECONDS),
                                     actor, "TimeOut"))
  }

  def getClientForClientId( clientId: ClientId ) = {
    val actorRef = system.actorSelection(clientId.id)
    LOG.info("Found {} reference for client {}.", actorRef, clientId, null)
    actorRef
  }

}
