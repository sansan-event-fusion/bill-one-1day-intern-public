@file:OptIn(KtorExperimentalLocationsAPI::class)

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.post
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import sender.application_service.SenderInvoiceRegisterService
import sender.domain.sender.SenderUUID
import sender.domain.sender_invoice.SenderInvoiceUUID
import sender.infrastructure.sender_invoice.SenderInvoiceStorage
import sender.util.getDomainEventContext
import java.util.*

@Location("/api/sender/sender/{senderUUID}/sender-invoices")
class InvoiceLocation(val senderUUID: UUID)

@Location("/api/sender/sender-invoices")
class SenderInvoicesLocation

@Location("/api/sender/sender/{senderUUID}/sender-invoices/{senderInvoiceUUID}/pdf-url")
class SenderInvoiceUrlLocation(val senderUUID: UUID, val senderInvoiceUUID: UUID)

fun Route.invoiceController() {
    // 課題1: 送付アカウント側の請求書一覧を取得する
    get<InvoiceLocation> {}

    // 課題2: 請求書のアップロード
    post<SenderInvoicesLocation> {
        SenderInvoiceRegisterService.register(call.getDomainEventContext())
        call.respond(HttpStatusCode.OK)
    }

    get<SenderInvoiceUrlLocation> {
        val pdfUrl =
            SenderInvoiceStorage.getUrl(
                SenderUUID(it.senderUUID),
                SenderInvoiceUUID(it.senderInvoiceUUID)
            )

        call.respondRedirect(pdfUrl.toString())
    }
}
