package com.achals.raft.communication

import akka.actor.{Cancellable, ActorRef, Props, ActorSystem}
import com.achals.raft.Node
import com.achals.raft.communication.Akka.Messages
import com.achals.raft.data.ClientId
import com.achals.raft.rpc.{ElectionVoteResponse, ElectionVoteRequest, AppendEntriesResponse, AppendEntriesRequest}
import org.joda.time.Seconds
import org.slf4j.LoggerFactory

/**
 * Created by achalshah on 9/19/14.
 */
class AkkaGateway(clientNode: Node, electionTimeout: Seconds) extends Gateway{

  val LOG = LoggerFactory.getLogger(this.getClass)

  val actor: ActorRef = AkkaActorSystem.getClient()
  val clientId = ClientId(actor.path.toStringWithoutAddress)

  LOG.info("Path for actor is {}.", this.actor)
  LOG.info("Cliend ID for the client is {}.", this.clientId)

  actor ! clientNode

  var cancellable:Option[Cancellable] = Option.empty

  def scheduleNewTimer() = {
    if (this.cancellable.isDefined) {
      this.cancellable.get.cancel()
    }
    this.cancellable = AkkaActorSystem.scheduleNewTimer(this.actor, this.electionTimeout)
  }

  @Override
  def appendEntries(clientId: ClientId, request: AppendEntriesRequest): AppendEntriesResponse = {
    null
  }

  @Override
  def requestVote(clientId: ClientId, request: ElectionVoteRequest): ElectionVoteResponse = {
    this.actor ! Messages.AkkaElectionVoteRequest(clientId, request)
    null
  }

  def respondToVoteResponse( sendingActor: ActorRef, response: ElectionVoteResponse ) = {
    this.clientNode.respondToVoteResponse(null,response)
  }
}
