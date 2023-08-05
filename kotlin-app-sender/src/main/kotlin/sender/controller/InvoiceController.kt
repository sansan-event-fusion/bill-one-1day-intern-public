@file:OptIn(KtorExperimentalLocationsAPI::class)

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.post
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.utils.io.core.*
import sender.application_service.InvoiceRegisterArgs
import sender.application_service.SenderInvoiceRegisterService
import sender.domain.recipient.RecipientUUID
import sender.domain.sender.SenderUUID
import sender.domain.sender_invoice.SenderInvoice
import sender.domain.sender_invoice.SenderInvoiceUUID
import sender.infrastructure.sender_invoice.SenderInvoiceStorage
import sender.query_service.SenderInvoiceQueryService
import sender.util.getDomainEventContext
import java.util.*
import kotlin.io.use

@Location("/api/sender/sender/{senderUUID}/sender-invoices")
class InvoiceLocation(val senderUUID: UUID)

@Location("/api/sender/sender-invoices")
class SenderInvoicesLocation

@Location("/api/sender/sender/{senderUUID}/sender-invoices/{senderInvoiceUUID}/pdf-url")
class SenderInvoiceUrlLocation(val senderUUID: UUID, val senderInvoiceUUID: UUID)

fun Route.invoiceController() {
    // 課題1: 送付アカウント側の請求書一覧を取得する
    get<InvoiceLocation> {
        val result = SenderInvoiceQueryService.getBySenderUUID(SenderUUID((it.senderUUID)))
        call.respond(result)
    }

    // 課題2: 請求書のアップロード
    post<SenderInvoicesLocation> {
        val multipartData = call.receiveMultipart()
        var senderUUID:SenderUUID? = null
        var recipientUUID:RecipientUUID? = null
        var pdf: ByteArray? = null
        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                     when(part.name){
                         "senderUUID" -> {
                            senderUUID = SenderUUID(UUID.fromString(part.value))
                        }
                         "recipientUUID" -> {
                             recipientUUID = RecipientUUID(UUID.fromString(part.value))
                         }
                    }
                }

                is PartData.FileItem -> {
                    if(part.name == "invoice"){
                        pdf = part.streamProvider().readAllBytes()
                    }
                }

                else -> {}
            }
            part.dispose()
        }
        if((recipientUUID != null) && (senderUUID != null) && (pdf != null)){
            SenderInvoiceRegisterService.register(InvoiceRegisterArgs(senderUUID!!, recipientUUID!!, pdf!!, call.getDomainEventContext()))
            call.respond(HttpStatusCode.OK)
        }else{
            call.respond(HttpStatusCode.BadRequest)
        }
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
