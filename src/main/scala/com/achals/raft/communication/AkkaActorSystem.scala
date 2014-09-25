package com.achals.raft.communication

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Props}
import com.achals.raft.data.ClientId
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration

/**
 * Created by achalshah on 9/21/14.
 */
object AkkaActorSystem {

    val LOG = LoggerFactory.getLogger (this.getClass.toString)

    val system = ActorSystem ("com-achals-raft")

    def getClient () = {
        system.actorOf (Props[CommunicatingActor])
    }

    def scheduleNewTimer (actor: ActorRef, electionTimeout: Int, message: String) = {
        Option (system.scheduler.schedule (new FiniteDuration (electionTimeout, TimeUnit.MILLISECONDS),
            new FiniteDuration (electionTimeout, TimeUnit.MILLISECONDS),
            actor, message))
    }

    def getClientForClientId (clientId: ClientId) = {
        val actorRef = system.actorSelection (clientId.id)
        LOG.debug ("Found {} reference for client {}.", actorRef, clientId, null)
        actorRef
    }

}
