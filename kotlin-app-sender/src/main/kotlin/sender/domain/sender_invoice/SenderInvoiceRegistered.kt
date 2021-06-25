package sender.domain.sender_invoice

import sender.domain.recipient.RecipientUUID
import sender.domain.sender.SenderUUID
import sender.domain_event.DomainEvent
import sender.util.StorageObjectPath

data class SenderInvoiceRegistered(
    val senderInvoiceUUID: SenderInvoiceUUID,
    val recipientUUID: RecipientUUID,
    val senderUUID:SenderUUID,
    val senderSideInvoicePath: StorageObjectPath
) : DomainEvent
