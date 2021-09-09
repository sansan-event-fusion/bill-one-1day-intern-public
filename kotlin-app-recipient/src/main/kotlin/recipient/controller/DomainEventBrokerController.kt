@file:OptIn(KtorExperimentalLocationsAPI::class)

package recipient.controller

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import recipient.domain_event.Broker
import recipient.domain_event.DomainEventContext
import recipient.domain_event.DomainEventRepository
import recipient.util.*
import java.util.*

@Location("/domain-event-broker/{callUUID}")
class DomainEventBrokerLocation(val callUUID: String)

fun Route.domainEventBroker() {

    post<DomainEventBrokerLocation> {

        val targetCallUUID = CallUUID(UUID.fromString(it.callUUID))

        deployDomainEvents(targetCallUUID, call.getTraceContext(), call.getDomainEventContext())

        call.respond(HttpStatusCode.OK)
    }
}

fun deployDomainEvents(targetCallUUID: CallUUID, traceContext: TraceContext, domainEventContext: DomainEventContext) {
    runInTransaction(domainEventContext) { handle ->

        val domainEventRepository = DomainEventRepository()

        val domainEvents = domainEventRepository.getByCallUUID(targetCallUUID, handle)

        domainEvents.forEach { domainEventRow ->

            if (domainEventRow.deployed) {
                return@forEach
            }

            val subscribers = Broker.pubsub[domainEventRow.domainEventName]
                ?: throw IllegalStateException("存在しないドメインイベントのキーが指定されました。 ${domainEventRow.domainEventName}")
            subscribers.forEach { subscriber -> subscriber.notify(domainEventRow.message, traceContext) }
        }

        domainEventRepository.markAsDeployed(targetCallUUID, handle)
    }
}
