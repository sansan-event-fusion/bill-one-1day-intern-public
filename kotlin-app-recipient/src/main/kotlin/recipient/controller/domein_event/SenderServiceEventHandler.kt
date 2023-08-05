@file:OptIn(KtorExperimentalLocationsAPI::class)

package recipient.controller.domein_event

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.post
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import recipient.application_service.InvoiceRegisterArgs
import recipient.application_service.InvoiceRegisterService
import recipient.domain.invoice.Invoice
import recipient.domain.invoice.SenderInvoiceUUID
import recipient.domain.recipient.RecipientUUID
import recipient.domain.sender.SenderUUID
import recipient.domain_event.Broker
import recipient.domain_event.DomainEventContext
import recipient.infrastructure.invoice.InvoiceRepository
import recipient.query_service.InvoiceQueryService
import recipient.util.StorageObjectPath
import recipient.util.getDomainEventContext
import java.util.*

@Location("/event-handler/sender/reflect-sender-invoice")
class SenderInvoiceReflectLocation

fun Route.senderServiceEventHandler() {
    // 課題3
    post<SenderInvoiceReflectLocation> {
        val body = call.receive<SenderInvoice>()
        InvoiceRegisterService.uploadFromSender(body.toInvoiceRegisterArgs(call.getDomainEventContext()))
        call.respond(HttpStatusCode.OK)
    }
}

data class SenderInvoice(
    val senderUUID: String,
    val recipientUUID: String,
    val senderInvoiceUUID: String,
    val memo: String,
    val senderSideInvoicePath: SenderSideInvoicePath
){
    fun toInvoiceRegisterArgs(domainEventContext: DomainEventContext):InvoiceRegisterArgs{
        return InvoiceRegisterArgs(
            SenderInvoiceUUID(UUID.fromString(senderInvoiceUUID)),
            RecipientUUID(UUID.fromString(recipientUUID)),
            SenderUUID(UUID.fromString(senderUUID)),
            memo,
            StorageObjectPath(senderSideInvoicePath.bucket, senderSideInvoicePath.path),
            domainEventContext
            )
    }
    data class SenderSideInvoicePath(
        val path: String,
        val bucket: String
    )
}

