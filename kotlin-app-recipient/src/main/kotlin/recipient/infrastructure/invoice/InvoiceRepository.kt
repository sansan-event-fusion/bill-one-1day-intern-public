package recipient.infrastructure.invoice

import org.jdbi.v3.core.Handle
import recipient.domain.invoice.Invoice

object InvoiceRepository {
    fun register(invoice: Invoice, handle: Handle) {
        val sql = """
            INSERT INTO invoice (
                invoice_uuid,
                sender_invoice_uuid,
                recipient_uuid,
                sender_uuid,
                registered_at,
                created_at
            ) VALUES (
                :invoiceUUID,
                :senderInvoiceUUID,
                :recipientUUID,
                :senderUUID,
                :registeredAt,
                now()
            )
        """.trimIndent()

        handle.createUpdate(sql)
            .bind("invoiceUUID", invoice.invoiceUUID.value)
            .bind("senderInvoiceUUID", invoice.senderInvoiceUUID.value)
            .bind("recipientUUID", invoice.recipientUUID.value)
            .bind("senderUUID", invoice.senderUUID.value)
            .bind("registeredAt", invoice.registeredAt.value)
            .execute()
    }
}
