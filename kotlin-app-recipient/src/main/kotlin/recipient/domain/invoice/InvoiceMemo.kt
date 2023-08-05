package recipient.domain.invoice

data class InvoiceMemo (
    val invoiceUUID: InvoiceUUID,
    val memo: String,
)

