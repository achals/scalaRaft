package com.achals.raft.communication

import akka.actor.{ActorRef, Cancellable}
import com.achals.raft.Node
import com.achals.raft.communication.Akka.Messages
import com.achals.raft.data.ClientId
import com.achals.raft.rpc.{AppendEntriesRequest, AppendEntriesResponse, ElectionVoteRequest, ElectionVoteResponse}
import org.slf4j.LoggerFactory

import scala.util.Random

/**
 * Created by achalshah on 9/19/14.
 */
class AkkaGateway (clientNode: Node) extends Gateway {

    val LOG = LoggerFactory.getLogger (this.getClass)

    val actor: ActorRef = AkkaActorSystem.getClient ()
    val clientId = ClientId (actor.path.toStringWithoutAddress)
    val timeoutMillis = this.randomTimeout ()

    LOG.info ("Path for actor is {}.", this.actor)
    LOG.info ("Cliend ID for the client is {}.", this.clientId)

    actor ! clientNode

    var cancellable: Option[Cancellable] = Option.empty

    def scheduleNewElectionTimer () = {
        if (this.cancellable.isDefined) {
            this.cancellable.get.cancel ()
        }
        LOG.info ("{} scheduling timeout for {} millis.", this.clientId, this.timeoutMillis)
        this.cancellable = AkkaActorSystem.scheduleNewTimer (this.actor, this.timeoutMillis, "TimeOut")
    }

    def scheduleNewHeartbeatTimer () = {
        if (this.cancellable.isDefined) {
            this.cancellable.get.cancel ()
        }
        LOG.info ("{} scheduling timeout for {} millis.", this.clientId, this.timeoutMillis)
        this.cancellable = AkkaActorSystem.scheduleNewTimer (this.actor, this.timeoutMillis, "HeartBeat")
    }

    @Override
    def appendEntries (clientId: ClientId, request: AppendEntriesRequest): AppendEntriesResponse = {
        this.actor ! Messages.AkkaAppendEntriesRequest (clientId, request)
        null
    }

    @Override
    def requestVote (clientId: ClientId, request: ElectionVoteRequest): ElectionVoteResponse = {
        this.actor ! Messages.AkkaElectionVoteRequest (clientId, request)
        null
    }

    def respondToVoteResponse (sendingActor: ActorRef, response: ElectionVoteResponse) = {
        this.clientNode.respondToVoteResponse (null, response)
    }

    def vote (clientId: ClientId, voteResponse: ElectionVoteResponse) = {
        this.actor ! Messages.AkkaElectionVote (clientId, voteResponse)
    }

    def randomTimeout () = {
        Random.nextInt (700) + 100
    }
}
