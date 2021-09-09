package sender.infrastructure.sender_invoice

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import sender.domain.sender.SenderUUID
import sender.domain.sender_invoice.SenderInvoice
import sender.domain.sender_invoice.SenderInvoiceUUID
import sender.settings
import sender.util.createStorageObject
import sender.util.createStorageUrl
import sender.util.getStorageObject
import java.net.URL

object SenderInvoiceStorage {
    fun upload(pdf:ByteArray, senderInvoice:SenderInvoice):BlobId{
        val blobId = BlobId.of(settings.senderInvoiceBucket, attachmentPath(senderInvoice.senderUUID, senderInvoice.senderInvoiceUUID))
        val blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/pdf").build()

        createStorageObject(blobInfo, pdf)

        return blobId
    }

    fun getUrl(senderUUID:SenderUUID, senderInvoiceUUID: SenderInvoiceUUID): URL {
        val blobId = BlobId.of(settings.senderInvoiceBucket, attachmentPath(senderUUID,senderInvoiceUUID))
        val blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/pdf").build()
        return createStorageUrl(blobInfo)
    }

    private fun attachmentPath (senderUUID: SenderUUID, senderInvoiceUUID: SenderInvoiceUUID):String{
       return  "sender/${senderUUID.value}/sender-invoices/${senderInvoiceUUID.value}.pdf"
    }
}