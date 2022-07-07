package recipient.domain_event

import recipient.util.CallUUID
import recipient.util.TraceContext

data class DomainEventContext(
    val callUUID: CallUUID,
    val traceContext: TraceContext
)
