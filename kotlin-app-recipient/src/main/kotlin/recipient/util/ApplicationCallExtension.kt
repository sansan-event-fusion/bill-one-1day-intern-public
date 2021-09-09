package recipient.util

import io.ktor.application.ApplicationCall
import io.ktor.features.callId
import recipient.domain_event.DomainEventContext
import java.util.*

fun ApplicationCall.getTraceContext(): TraceContext = TraceContext(
    request.headers["X-Cloud-Trace-Context"] ?: "00000000000000000000000000000000/0"
)

fun ApplicationCall.getCallUUID(): CallUUID = CallUUID(UUID.fromString(callId))

fun ApplicationCall.getDomainEventContext() = DomainEventContext(getCallUUID(), getTraceContext())
