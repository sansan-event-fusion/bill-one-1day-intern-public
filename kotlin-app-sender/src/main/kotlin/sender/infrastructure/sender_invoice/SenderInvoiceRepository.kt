package sender.infrastructure.sender_invoice

import org.jdbi.v3.core.Handle
import sender.domain.sender_invoice.SenderInvoice

object SenderInvoiceRepository {
    fun register(senderInvoice: SenderInvoice, handle: Handle) {
        val sql = """
            INSERT INTO sender_invoice (
                sender_invoice_uuid,
                recipient_uuid,
                sender_uuid,
                registered_at,
                created_at
            ) VALUES (
                :senderInvoiceUUID,
                :recipientUUID,
                :senderUUID,
                :registeredAt,
                now()
            )
        """.trimIndent()

        senderInvoice.let {
            handle.createUpdate(sql)
                .bind("senderInvoiceUUID", it.senderInvoiceUUID.value)
                .bind("recipientUUID", it.recipientUUID.value)
                .bind("senderUUID", it.senderUUID.value)
                .bind("registeredAt", it.registeredAt.value)
                .execute()
        }
    }
}
