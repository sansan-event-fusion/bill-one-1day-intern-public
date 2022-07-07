@file:OptIn(KtorExperimentalLocationsAPI::class)

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import sender.query_service.SenderQueryService
import java.util.*

@Location("/api/sender/senders")
class SendersLocation

fun Route.senderController() {
    get<SendersLocation> {
        val result = SenderQueryService.getAll()

        call.respond(result)
    }
}
