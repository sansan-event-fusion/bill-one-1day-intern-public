package sender.domain_event.subscriber

import sender.util.TraceContext

interface Subscriber {
    fun notify(message: String, traceContext: TraceContext)
}
