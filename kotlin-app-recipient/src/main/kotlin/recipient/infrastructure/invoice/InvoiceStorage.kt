package recipient.infrastructure.invoice

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import recipient.domain.invoice.Invoice
import recipient.domain.invoice.InvoiceUUID
import recipient.domain.recipient.RecipientUUID
import recipient.settings
import recipient.util.StorageObjectPath
import recipient.util.copyStorageObject
import recipient.util.createStorageUrl
import java.net.URL

object InvoiceStorage {
    fun copyFromSenderInvoice(senderSideInvoicePath: StorageObjectPath, invoice: Invoice) {
        val targetBlobId =
            BlobId.of(settings.recipientInvoiceBucket, attachmentPath(invoice.recipientUUID, invoice.invoiceUUID))

        copyStorageObject(senderSideInvoicePath.toBlobId(), targetBlobId)
    }

    fun getUrl(recipientUUID: RecipientUUID, invoiceUUID: InvoiceUUID): URL {
        val blobId = BlobId.of(settings.recipientInvoiceBucket, attachmentPath(recipientUUID, invoiceUUID))
        val blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/pdf").build()
        return createStorageUrl(blobInfo)
    }

    private fun attachmentPath(recipientUUID: RecipientUUID, invoiceUUID: InvoiceUUID): String {
        return "recipient/${recipientUUID.value}/invoices/${invoiceUUID.value}.pdf"
    }
}
