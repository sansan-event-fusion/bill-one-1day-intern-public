@file:OptIn(KtorExperimentalLocationsAPI::class)

package recipient.controller.domein_event

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.post
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

@Location("/event-handler/sender/reflect-sender-invoice")
class SenderInvoiceReflectLocation

fun Route.senderServiceEventHandler() {
    // 課題3
    post<SenderInvoiceReflectLocation> {}
}
