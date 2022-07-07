package sender.domain_event.subscriber

import com.google.cloud.tasks.v2.*
import com.google.cloud.tasks.v2.HttpMethod
import com.google.protobuf.ByteString
import io.ktor.http.*
import org.slf4j.LoggerFactory
import sender.settings
import sender.util.TraceContext
import sender.util.buildCloudTasksSettings
import java.nio.charset.StandardCharsets

class SenderInvoiceReflect : Subscriber {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun notify(message: String, traceContext: TraceContext) {
        val oidcTokenBuilder = OidcToken.newBuilder().setServiceAccountEmail(settings.serviceAccount)

        val task = Task.newBuilder()
            .setHttpRequest(
                HttpRequest.newBuilder()
                    .putHeaders("X-Cloud-Trace-Context", traceContext.value)
                    .putHeaders(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    .setBody(ByteString.copyFrom(message, StandardCharsets.UTF_8))
                    .setUrl("${settings.recipientApiUrl}/event-handler/sender/reflect-sender-invoice")
                    .setOidcToken(oidcTokenBuilder)
                    .setHttpMethod(HttpMethod.POST)
                    .build()
            ).build()

        CloudTasksClient.create(buildCloudTasksSettings()).use { client ->
            val createdTask = client.createTask(settings.senderServiceQueryPath, task)
            logger.info("タスクの作成に成功しました。 name: ${createdTask.name}")
        }
    }
}
