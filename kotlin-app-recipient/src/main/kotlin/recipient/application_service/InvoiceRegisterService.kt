package recipient.application_service

import recipient.domain.invoice.Invoice
import recipient.domain.invoice.InvoiceMemo
import recipient.domain.invoice.SenderInvoiceUUID
import recipient.domain.recipient.RecipientUUID
import recipient.domain.sender.SenderUUID
import recipient.domain_event.DomainEventContext
import recipient.infrastructure.invoice.InvoiceRepository
import recipient.infrastructure.invoice.InvoiceStorage
import recipient.util.StorageObjectPath
import recipient.util.runInTransaction

object InvoiceRegisterService {
    fun uploadFromSender(args: InvoiceRegisterArgs) {
        val invoice = Invoice.of(
            args.senderInvoiceUUID,
            args.recipientUUID,
            args.senderUUID
        )
        val invoiceMemo = InvoiceMemo(invoice.invoiceUUID, args.memo)
        runInTransaction(args.domainEventContext) { handle ->
            InvoiceRepository.register(invoice, handle)
            InvoiceRepository.registerMemo(invoiceMemo, handle)
            InvoiceStorage.copyFromSenderInvoice(args.senderSideInvoicePath, invoice)
        }
    }
}

data class InvoiceRegisterArgs(
    val senderInvoiceUUID: SenderInvoiceUUID,
    val recipientUUID: RecipientUUID,
    val senderUUID: SenderUUID,
    val memo: String,
    val senderSideInvoicePath: StorageObjectPath,
    val domainEventContext: DomainEventContext
)
