package sender.domain.sender_invoice

data class SenderInvoiceMemo (
    val senderInvoiceUUID: SenderInvoiceUUID,
    val memo: String,
)