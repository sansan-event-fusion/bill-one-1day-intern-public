package sender.application_service

import sender.domain.recipient.RecipientUUID
import sender.domain.sender.SenderUUID
import sender.domain.sender_invoice.SenderInvoice
import sender.domain.sender_invoice.SenderInvoiceUUID
import sender.domain_event.DomainEventContext
import sender.infrastructure.sender_invoice.SenderInvoiceRepository
import sender.infrastructure.sender_invoice.SenderInvoiceStorage
import sender.util.runInTransaction

object SenderInvoiceRegisterService {
    // 課題2
    fun register(args: InvoiceRegisterArgs){
        val invoice = SenderInvoice.of(
            args.recipientUUID,
            args.senderUUID,
        )
        runInTransaction(args.domainEventContext) { handle ->
            SenderInvoiceRepository.register(invoice, handle)
            SenderInvoiceStorage.upload(args.pdf, invoice)
        }
    }
}

data class InvoiceRegisterArgs(
    val senderUUID: SenderUUID,
    val recipientUUID: RecipientUUID,
    val pdf: ByteArray,
    val domainEventContext: DomainEventContext,
)