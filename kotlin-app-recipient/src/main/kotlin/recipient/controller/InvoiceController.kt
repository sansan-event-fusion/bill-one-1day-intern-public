@file:OptIn(KtorExperimentalLocationsAPI::class)

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import recipient.domain.invoice.InvoiceUUID
import recipient.domain.recipient.RecipientUUID
import recipient.infrastructure.invoice.InvoiceStorage
import recipient.query_service.InvoiceQueryService
import java.util.*

@Location("/api/recipient/recipient/{recipientUUID}/invoices")
class InvoiceLocation(
    val recipientUUID: UUID
)

@Location("/api/recipient/recipient/{recipientUUID}/invoices/{invoiceUUID}/pdf-url")
class InvoiceUrlLocation(
    val recipientUUID: UUID,
    val invoiceUUID: UUID
)

fun Route.invoiceController() {
    get<InvoiceLocation> {
        val result = InvoiceQueryService.getByRecipientUUID(RecipientUUID(it.recipientUUID))

        call.respond(result)
    }

    get<InvoiceUrlLocation> {
        val pdfUrl = InvoiceStorage.getUrl(RecipientUUID(it.recipientUUID), InvoiceUUID(it.invoiceUUID))

        call.respondRedirect(pdfUrl.toString())
    }
}
