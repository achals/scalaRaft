package com.achals.raft.communication

import akka.actor.Actor
import com.achals.raft.Node
import com.achals.raft.communication.Akka.Messages
import com.achals.raft.data.ClientId
import com.achals.raft.rpc.{AppendEntriesRequest, ElectionVoteRequest, ElectionVoteResponse}

/**
 * Created by achalshah on 9/19/14.
 */
class CommunicatingActor extends Actor {

    var clientNode: Node = null

    def receive = {
        case clientNode: Node => {
            this.clientNode = clientNode
        }

        case "TimeOut" => this.clientNode.contestForLeader ()

        case "HeartBeat" => this.clientNode.sendHeartbeatToAllServers ()

        case Messages.AkkaAppendEntriesRequest (clientId, request) => {
            AkkaActorSystem.getClientForClientId (clientId) ! request
        }

        case request: AppendEntriesRequest => {
            this.clientNode.respondToAppendEntriesRequest (ClientId (sender ().path.toStringWithoutAddress), request)
        }

        case Messages.AkkaElectionVoteRequest (clientId, request) => {
            AkkaActorSystem.getClientForClientId (clientId) ! request
        }

        case request: ElectionVoteRequest => {
            this.clientNode.servers.+= (ClientId (sender ().path.toStringWithoutAddress))
            this.clientNode.respondToVoteRequest (ClientId (sender ().path.toStringWithoutAddress), request)
        }

        case Messages.AkkaElectionVote (clientId, response) => {
            AkkaActorSystem.getClientForClientId (clientId) ! response
        }

        case response: ElectionVoteResponse => {
            this.clientNode.servers.+= (ClientId (sender ().path.toStringWithoutAddress))
            this.clientNode.respondToVoteResponse (ClientId (sender ().path.toStringWithoutAddress), response)
        }

        case x => println (x)
    }
}
