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
    val memo: String,
    val senderSideInvoicePath: StorageObjectPath
) : DomainEvent{
    companion object {
        fun of(senderInvoice: SenderInvoice, senderInvoiceMemo: SenderInvoiceMemo, blobId: BlobId) = SenderInvoiceRegistered(
            senderInvoice.senderInvoiceUUID,
            senderInvoice.recipientUUID,
            senderInvoice.senderUUID,
            senderInvoiceMemo.memo,
            StorageObjectPath.fromBlobId(blobId)
        )
    }
}
