package recipient.domain_event.subscriber

import com.google.cloud.tasks.v2.*
import com.google.protobuf.ByteString
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import org.slf4j.LoggerFactory
import recipient.settings
import recipient.util.TraceContext
import recipient.util.buildCloudTasksSettings
import java.nio.charset.StandardCharsets

open class CloudRunSubscriber(private val url: String, private val queuePath: String) : Subscriber {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun notify(message: String,traceContext: TraceContext) {
        val oidcTokenBuilder = OidcToken.newBuilder().setServiceAccountEmail(settings.serviceAccount)

        val task = Task.newBuilder()
            .setHttpRequest(
                HttpRequest.newBuilder()
                    .putHeaders("X-Cloud-Trace-Context", traceContext.value)
                    .putHeaders(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    .setBody(ByteString.copyFrom(message, StandardCharsets.UTF_8))
                    .setUrl(url)
                    .setOidcToken(oidcTokenBuilder)
                    .setHttpMethod(HttpMethod.POST)
                    .build()).build()

        CloudTasksClient.create(buildCloudTasksSettings()).use { client ->
            val createdTask = client.createTask(queuePath, task)
            logger.info("タスクの作成に成功しました。 name: ${createdTask.name} url: $url")
        }
    }
}
