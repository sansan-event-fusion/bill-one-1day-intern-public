@file:OptIn(KtorExperimentalLocationsAPI::class)

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import recipient.query_service.RecipientQueryService

@Location("/api/recipient/recipients")
class RecipientsLocation

fun Route.recipientController() {
    get<RecipientsLocation> {
        val result = RecipientQueryService.getAll()

        call.respond(result)
    }
}
