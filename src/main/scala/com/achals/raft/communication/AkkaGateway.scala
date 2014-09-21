package com.achals.raft.communication

import java.util.concurrent.TimeUnit

import akka.actor.{Cancellable, ActorRef, Props, ActorSystem}
import com.achals.raft.Node
import com.achals.raft.data.ClientId
import com.achals.raft.rpc.{ElectionVoteResponse, ElectionVoteRequest, AppendEntriesResponse, AppendEntriesRequest}
import org.joda.time.Seconds
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration

/**
 * Created by achalshah on 9/19/14.
 */
class AkkaGateway(clientNode: Node, electionTimeout: Seconds) extends Gateway{

  val LOG = LoggerFactory.getLogger(this.getClass)

  val system = ActorSystem(clientNode.clientId.id)
  val actor: ActorRef = system.actorOf(Props[CommunicatingActor])
  LOG.info("Path for actor is {}.", this.actor)
  actor ! clientNode

  var cancellable:Option[Cancellable] = Option.empty

  var serverActors: Map[ClientId, ActorRef] = Map()

  def scheduleNewTimer() = {
    if (this.cancellable.isDefined) {
      this.cancellable.get.cancel()
    }
    this.cancellable = Option(system.scheduler.schedule(new FiniteDuration(electionTimeout.getSeconds, TimeUnit.SECONDS),
                                                        new FiniteDuration(electionTimeout.getSeconds, TimeUnit.SECONDS),
                                                        actor, "TimeOut"))
  }

  @Override
  def appendEntries(clientId: ClientId, request: AppendEntriesRequest): AppendEntriesResponse = {
    null
  }

  @Override
  def requestVote(clientId: ClientId, request: ElectionVoteRequest): ElectionVoteResponse = {
    this.serverActors.get(clientId).get ! request
    null
  }

  def respondToVoteResponse( sendingActor: ActorRef, response: ElectionVoteResponse ) = {
    this.clientNode.respondToVoteResponse(null,response)
  }
}
