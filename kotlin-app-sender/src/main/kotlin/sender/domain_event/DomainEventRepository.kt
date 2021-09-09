package sender.domain_event

import org.jdbi.v3.core.Handle
import org.postgresql.util.PGobject
import sender.util.CallUUID
import java.util.*

object DomainEventRepository {

    fun publish(domainEvent: DomainEvent, callUUID: CallUUID, handle: Handle) {
        publish(listOf(domainEvent),  callUUID, handle)
    }

    fun publish(domainEvents: List<DomainEvent>,  callUUID: CallUUID, handle: Handle) {

        val sql = """
            INSERT INTO domain_event (
                domain_event_uuid,
                call_uuid,
                domain_event_name,
                message,
                deployed,
                created_at
            ) VALUES (
                :domainEventUUID,
                :callUUID,
                :domainEventName,
                :message,
                false,
                now()
            )
        """.trimIndent()

        val batch = handle.prepareBatch(sql)
        domainEvents.forEach { domainEvent ->
            batch
                .bind("domainEventUUID", UUID.randomUUID())
                .bind("callUUID", callUUID.value)
                .bind("domainEventName", domainEvent.javaClass.kotlin.qualifiedName)
                .bind("message", PGobject().apply {
                    type = "json"
                    value = domainEvent.toJSON()
                })
                .add()
        }

        batch.execute()
    }

    fun getByCallUUID(callUUID: CallUUID, handle: Handle): List<DomainEventRow> {

        val selectPaymentSQL = """
            SELECT
                domain_event_uuid as domainEventUUID,
                call_uuid as callUUID,
                domain_event_name as domainEventName,
                message,
                deployed
            FROM domain_event
            WHERE call_uuid = :callUUID
            ORDER BY created_at
        """.trimIndent()

        return handle.createQuery(selectPaymentSQL)
            .bind("callUUID", callUUID.value)
            .mapTo(DomainEventRow::class.java)
            .list()
    }

    fun markAsDeployed(callUUID: CallUUID, handle: Handle) {
        val sql = """
            UPDATE 
                domain_event
            SET 
                deployed = true
            WHERE 
                call_uuid = :callUUID
        """.trimIndent()

        handle.createUpdate(sql)
            .bind("callUUID", callUUID.value)
            .execute()
    }
}
