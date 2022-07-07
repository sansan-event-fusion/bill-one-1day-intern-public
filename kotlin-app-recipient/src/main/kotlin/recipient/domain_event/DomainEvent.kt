package recipient.domain_event

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.*

interface DomainEvent {
    fun toJSON(): String = jacksonObjectMapper().writeValueAsString(this)
}

data class DomainEventRow(
    val domainEventUUID: UUID,
    val callUUID: UUID,
    val domainEventName: String,
    val message: String,
    val deployed: Boolean
)
