package sender.query_service

import io.ktor.http.*
import sender.domain.recipient.RecipientUUID
import sender.domain.sender.SenderUUID
import sender.domain.sender_invoice.SenderInvoice
import sender.domain_event.DomainEventContext
import sender.infrastructure.sender_invoice.SenderInvoiceRepository
import sender.util.runInTransaction
import sender.util.withHandle
import java.util.*

object SenderInvoiceQueryService{
    fun getBySenderUUID(senderUUID: SenderUUID): List<InvoiceQueryGetResult> {
        val sql = """
            SELECT
                i.sender_invoice_uuid,
                r.full_name as recipient_full_name,
                s.full_name as sender_full_name,
                i.registered_at,
                m.memo
            FROM sender_invoice i
                INNER JOIN recipient r ON i.recipient_uuid = r.recipient_uuid
                INNER JOIN sender s ON i.sender_uuid = s.sender_uuid
                LEFT JOIN sender_invoice_memo m ON i.sender_invoice_uuid = m.sender_invoice_uuid
            WHERE i.sender_uuid = :senderUUID
        """.trimIndent()

        withHandle { handle ->
            return handle.createQuery(sql)
                .bind("senderUUID", senderUUID.value)
                .mapTo(InvoiceQueryGetResult::class.java)
                .list()
        }
    }
}

data class InvoiceQueryGetResult(
    val senderInvoiceUUID: UUID,
    val recipientFullName: String,
    val senderFullName: String,
    val registeredAt: String,
    val memo: String?
)
