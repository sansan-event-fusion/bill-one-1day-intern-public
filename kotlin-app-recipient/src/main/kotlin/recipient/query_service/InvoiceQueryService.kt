package recipient.query_service

import recipient.domain.recipient.RecipientUUID
import recipient.util.withHandle
import java.util.*

object InvoiceQueryService {
    fun getByRecipientUUID(recipientUUID: RecipientUUID): List<InvoiceQueryGetResult> {
        val sql = """
            SELECT
                i.invoice_uuid,
                r.full_name as recipient_full_name,
                s.full_name as sender_full_name,
                m.memo,
                i.registered_at
            FROM invoice i
                INNER JOIN recipient r ON i.recipient_uuid = r.recipient_uuid
                INNER JOIN sender s ON i.sender_uuid = s.sender_uuid
                LEFT JOIN invoice_memo m ON i.invoice_uuid = m.invoice_uuid
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
    val memo: String?,
    val registeredAt: String
)
