package sender.application_service

import sender.domain.recipient.RecipientUUID
import sender.domain.sender.SenderUUID
import sender.domain.sender_invoice.SenderInvoice
import sender.domain.sender_invoice.SenderInvoiceMemo
import sender.domain.sender_invoice.SenderInvoiceRegistered
import sender.domain.sender_invoice.SenderInvoiceUUID
import sender.domain_event.DomainEventContext
import sender.domain_event.DomainEventRepository
import sender.infrastructure.sender_invoice.SenderInvoiceRepository
import sender.infrastructure.sender_invoice.SenderInvoiceStorage
import sender.util.CallUUID
import sender.util.runInTransaction
import java.util.*

object SenderInvoiceRegisterService {
    // 課題2
    fun register(args: InvoiceRegisterArgs){
        val invoice = SenderInvoice.of(
            args.recipientUUID,
            args.senderUUID,
        )
        val invoiceMemo = SenderInvoiceMemo(invoice.senderInvoiceUUID, args.memo)
        runInTransaction(args.domainEventContext) { handle ->
            SenderInvoiceRepository.register(invoice, handle)
            SenderInvoiceRepository.registerMemo(invoiceMemo, handle)
            val blobId = SenderInvoiceStorage.upload(args.pdf, invoice)
            DomainEventRepository.publish(SenderInvoiceRegistered.of(invoice, invoiceMemo, blobId), args.domainEventContext.callUUID, handle)
        }
    }
}

data class InvoiceRegisterArgs(
    val senderUUID: SenderUUID,
    val recipientUUID: RecipientUUID,
    val memo: String,
    val pdf: ByteArray,
    val domainEventContext: DomainEventContext,
)