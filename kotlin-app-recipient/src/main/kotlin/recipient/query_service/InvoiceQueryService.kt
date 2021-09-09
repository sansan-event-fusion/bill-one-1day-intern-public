package recipient.query_service

import recipient.domain.invoice.RegisteredAt
import recipient.domain.recipient.RecipientFullName
import recipient.domain.recipient.RecipientUUID
import recipient.domain.sender.SenderFullName
import recipient.util.withHandle
import java.time.OffsetDateTime
import java.util.*

object InvoiceQueryService {
    fun getByRecipientUUID(recipientUUID: RecipientUUID): List<InvoiceQueryGetResult>{
        val sql = """
            SELECT
                i.invoice_uuid,
                r.full_name as recipient_full_name,
                s.full_name as sender_full_name,
                i.registered_at
            FROM invoice i
                INNER JOIN recipient r ON i.recipient_uuid = r.recipient_uuid
                INNER JOIN sender s ON i.sender_uuid = s.sender_uuid
            WHERE i.recipient_uuid = :recipientUUID
        """.trimIndent()

        withHandle { handle ->
            return handle.createQuery(sql)
                .bind("recipientUUID", recipientUUID.value)
                .mapTo(InvoiceQueryGetResult::class.java)
                .list()
        }
    }
}

data class InvoiceQueryGetResult(
    val invoiceUUID: UUID,
    val recipientFullName: String,
    val senderFullName: String,
    val registeredAt: String
)