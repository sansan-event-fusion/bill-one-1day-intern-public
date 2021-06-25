package recipient.domain_event.subscriber

import recipient.util.TraceContext

interface Subscriber {
    fun notify(message: String,  traceContext: TraceContext)
}
