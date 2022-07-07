package recipient.domain_event

import com.google.cloud.tasks.v2.CloudTasksClient
import com.google.cloud.tasks.v2.HttpMethod
import com.google.cloud.tasks.v2.HttpRequest
import com.google.cloud.tasks.v2.OidcToken
import com.google.cloud.tasks.v2.Task
import io.ktor.http.*
import org.slf4j.LoggerFactory
import recipient.domain_event.subscriber.Subscriber
import recipient.settings
import recipient.util.buildCloudTasksSettings

class Broker {

    private val logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        val pubsub = emptyMap<String, List<Subscriber>>()

//            mapOf(

//            RemovedFromRecipientGroup::class.qualifiedName to listOf(TenantServiceSubscriber("notify-removed-from-recipient-group")),
//        )
    }

    fun createTask(domainEventContext: DomainEventContext) {
        val oidcTokenBuilder = OidcToken.newBuilder().setServiceAccountEmail(settings.serviceAccount)

        val retryCount = 3

        val task = Task.newBuilder()
            .setHttpRequest(
                HttpRequest.newBuilder()
                    .putHeaders("X-Cloud-Trace-Context", domainEventContext.traceContext.value)
                    .putHeaders(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    .setUrl("${settings.invoiceApiUrl}/domain-event-broker/${domainEventContext.callUUID.value}")
                    .setOidcToken(oidcTokenBuilder)
                    .setHttpMethod(HttpMethod.POST)
                    .build()
            ).build()

        CloudTasksClient.create(buildCloudTasksSettings()).use { client ->

            for (i in 1..retryCount) {
                try {
                    val createdTask = client.createTask(settings.recipientServiceQueryPath, task)
                    logger.info("タスクの作成に成功しました。 name: ${createdTask.name}")
                    break
                } catch (e: Exception) {
                    if (i == retryCount) {
                        throw e
                    }
                }
            }
        }
    }
}
