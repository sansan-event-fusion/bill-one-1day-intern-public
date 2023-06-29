@file:OptIn(KtorExperimentalLocationsAPI::class)

package recipient

import invoiceController
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.*
import io.ktor.util.*
import org.slf4j.event.Level
import recipient.controller.domainEventBroker
import recipient.controller.domein_event.senderServiceEventHandler
import recipient.util.configureObjectMapper
import recipientController
import java.io.PrintWriter
import java.io.StringWriter
import java.time.LocalDate
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
// Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false, settingsTest: Settings = settingsFromEnv()) {
    settings = settingsTest

    install(CallLogging) {
        level = Level.INFO
    }

    install(ShutDownUrl.ApplicationCallFeature) {
        // The URL that will be intercepted. You can also use the
        // application.conf's ktor.deployment.shutdown.url key.
        shutDownUrl = "/_ah/stop"

        // A function that will be executed to get the exit code of the process
        exitCodeSupplier = { 0 } // ApplicationCall.() -> Int
    }

    // 型のついたLocation
    install(Locations)
    // JSON
    install(ContentNegotiation) {
        jackson {
            configureObjectMapper(this)
        }
        jackson(ContentType("application", "merge-patch+json")) {
            // objectMapperは同じ設定を使用する
            configureObjectMapper(this)
        }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Patch)
        method(HttpMethod.Post)
        method(HttpMethod.Delete)
        header(HttpHeaders.ContentType)
        anyHost()
        allowCredentials = true
        allowNonSimpleContentTypes = true
    }

    install(StatusPages) {
        // StackdriverのtextPayloadにStackTraceの情報が出力されるが、textPayloadの制限として5120文字で切り捨てられることにより
        // Jsonフォーマットが崩れてしまう。そのため、ErrorReportingに検知されない現状が発生する。
        // そのため、4500文字で分割出力する
        exception<Throwable> { cause ->

            val sw = StringWriter()
            val pw = PrintWriter(sw)
            cause.printStackTrace(pw)
            pw.flush()

            val chunkStackTrace = sw.toString().chunked(4500)

            for ((i, stackTrace) in chunkStackTrace.withIndex()) {
                if (i == 0) log.error(stackTrace) else log.info(stackTrace)
            }

            @OptIn(EngineAPI::class)
            call.respond(
                defaultExceptionStatusCode(cause) ?: HttpStatusCode.InternalServerError
            )

            // コントローラテストでの例外発生時にエラー内容を確認したいために、再スローする
            if (testing) {
                throw cause
            }
        }
    }

    install(CallId) {
        generate { UUID.randomUUID().toString() }
    }

    install(DataConversion) {
        convert<UUID> {
            // this: DelegatingConversionService

            decode { values, _ ->
                // converter: (values: List<String>, type: Type) -> Any?
                values.singleOrNull()?.let { UUID.fromString(it) }
            }

            encode { value ->
                // converter: (value: Any?) -> List<String>
                when (value) {
                    null -> listOf()
                    is UUID -> listOf(value.toString())
                    else -> throw DataConversionException("Cannot convert $value as UUID")
                }
            }
        }
        convert<Int> {
            // this: DelegatingConversionService

            decode { values, _ ->
                // converter: (values: List<String>, type: Type) -> Any?
                values.singleOrNull { it.isNotEmpty() }?.toInt()
            }

            encode { value ->
                // converter: (value: Any?) -> List<String>
                when (value) {
                    null -> listOf()
                    is Int -> listOf(value.toString())
                    else -> throw DataConversionException("Cannot convert $value as Int")
                }
            }
        }
        convert<Long> {
            // this: DelegatingConversionService

            decode { values, _ ->
                // converter: (values: List<String>, type: Type) -> Any?
                values.singleOrNull { it.isNotEmpty() }?.toLong()
            }

            encode { value ->
                // converter: (value: Any?) -> List<String>
                when (value) {
                    null -> listOf()
                    is Long -> listOf(value.toString())
                    else -> throw DataConversionException("Cannot convert $value as Long")
                }
            }
        }

        convert<LocalDate> {
            // this: DelegatingConversionService

            decode { values, _ ->
                // converter: (values: List<String>, type: Type) -> Any?
                values.singleOrNull { it.isNotEmpty() }?.let { LocalDate.parse(it) }
            }

            encode { value ->
                // converter: (value: Any?) -> List<String>
                when (value) {
                    null -> listOf()
                    is LocalDate -> listOf(value.toString())
                    else -> throw DataConversionException("Cannot convert $value as LocalDate")
                }
            }
        }
    }

    routing {
        invoiceController()
        recipientController()
        senderServiceEventHandler()
        domainEventBroker()
        get("/") {
            call.respondText("bill-one-1day-intern kotlin-app-recipient is up!", contentType = ContentType.Text.Plain)
        }
    }
}

lateinit var settings: Settings // テストのたびにSettingsを上書きできるようvarにする。
    private set
