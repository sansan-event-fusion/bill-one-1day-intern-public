package sender.domain_event

import sender.util.CallUUID
import sender.util.TraceContext

data class DomainEventContext(
    val callUUID: CallUUID,
    val traceContext: TraceContext,
)
