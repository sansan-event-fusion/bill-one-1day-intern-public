package sender.domain.sender_invoice

import com.google.cloud.storage.BlobId
import sender.domain.recipient.RecipientUUID
import sender.domain.sender.SenderUUID
import sender.domain_event.DomainEvent
import sender.util.StorageObjectPath

data class SenderInvoiceRegistered(
    val senderInvoiceUUID: SenderInvoiceUUID,
    val recipientUUID: RecipientUUID,
    val senderUUID: SenderUUID,
    val senderSideInvoicePath: StorageObjectPath
) : DomainEvent{
    companion object {
        fun of(senderInvoice: SenderInvoice, blobId: BlobId) = SenderInvoiceRegistered(
            senderInvoice.senderInvoiceUUID,
            senderInvoice.recipientUUID,
            senderInvoice.senderUUID,
            StorageObjectPath.fromBlobId(blobId)
        )
    }
}
