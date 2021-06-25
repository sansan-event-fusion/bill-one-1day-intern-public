@file:OptIn(KtorExperimentalLocationsAPI::class)

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import sender.application_service.SenderInvoiceRegisterService
import sender.domain.recipient.RecipientUUID
import sender.domain.sender.SenderUUID
import sender.domain.sender_invoice.SenderInvoiceUUID
import sender.infrastructure.sender_invoice.SenderInvoiceStorage
import sender.query_service.SenderInvoiceQueryService
import sender.query_service.SenderQueryService
import sender.util.getDomainEventContext
import java.util.*


@Location("/api/sender/senders")
class SendersLocation

fun Route.senderController() {
    get<SendersLocation>{
        val result = SenderQueryService.getAll()

        call.respond(result)
    }
}



