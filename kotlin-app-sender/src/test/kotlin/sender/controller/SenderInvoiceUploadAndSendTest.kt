package sender.controller

import com.google.cloud.storage.BlobId
import com.google.cloud.tasks.v2.CloudTasksClient
import com.google.cloud.tasks.v2.CloudTasksSettings
import com.google.cloud.tasks.v2.Task
import io.github.orangain.jsonmatch.JsonStringAssert
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.testing.*
import io.ktor.utils.io.core.internal.*
import io.ktor.utils.io.streams.*
import io.mockk.*
import org.assertj.core.api.Assertions
import org.jdbi.v3.core.Handle
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test
import sender.domain_event.DomainEventRepository
import sender.fixture.table.*
import sender.module
import sender.testing.Database
import sender.testing.testSettings
import sender.util.getCallUUID
import sender.util.getStorageObject
import sender.util.withHandle
import java.util.*

class SenderInvoiceUploadAndSendTest {
    companion object {
        private val schemaName = this::class.java.declaringClass.simpleName.lowercase()
        private val settings = testSettings(schemaName)
        private val database = Database(settings, schemaName)

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            database.tearDown()
        }
    }

    @Before
    fun before() {
        database.setUpTable(
            listOf(
                buildRecipientOperation(listOf(RecipientTableFixture())),
                buildSenderOperation(listOf(SenderTableFixture()))
            )
        )

        // Cloud Tasks のタスク作成をモックする
        mockkStatic(CloudTasksClient::class)
        every { CloudTasksClient.create(any<CloudTasksSettings>()).close() } just Runs
        every {
            CloudTasksClient.create(any<CloudTasksSettings>()).createTask(any<String>(), any())
        } returns Task.newBuilder().build()
    }

    @OptIn(DangerousInternalIoApi::class)
    @Test
    fun `送付アカウントアップロードのテスト`(): Unit =
        withTestApplication({ module(true, settings) }) {
            with(
                handleRequest(HttpMethod.Post, "/api/sender/sender-invoices") {
                    val boundary = "***bbb***"

                    addHeader(
                        HttpHeaders.ContentType,
                        ContentType.MultiPart.FormData.withParameter(
                            "boundary",
                            boundary
                        )
                            .toString()
                    )
                    addHeader(
                        "X-App-Sender-UUID",
                        SenderTableFixture().sender_uuid.toString()
                    )
                    addHeader(
                        "X-Cloud-Trace-Context",
                        "430963703d1a5d41fa9f63cda15a214d/0;o=1"
                    )
                    setBody(
                        boundary,
                        listOf(
                            PartData.FileItem(
                                {
                                    javaClass.classLoader.getResource(
                                        "pdf/upload.pdf"
                                    )!!
                                        .readBytes()
                                        .inputStream()
                                        .asInput()
                                },
                                {},
                                headersOf(
                                    HttpHeaders.ContentDisposition,
                                    ContentDisposition.File.withParameter(
                                        ContentDisposition
                                            .Parameters
                                            .Name,
                                        "invoice"
                                    )
                                        .withParameter(
                                            ContentDisposition
                                                .Parameters
                                                .FileName,
                                            "upload.pdf"
                                        )
                                        .toString()
                                )
                            ),
                            PartData.FormItem(
                                RecipientTableFixture()
                                    .recipient_uuid
                                    .toString(),
                                {},
                                headersOf(
                                    HttpHeaders.ContentDisposition,
                                    ContentDisposition.Inline.withParameter(
                                        ContentDisposition
                                            .Parameters
                                            .Name,
                                        "recipientUUID"
                                    )
                                        .toString()
                                )
                            ),
                            PartData.FormItem(
                                SenderTableFixture().sender_uuid.toString(),
                                {},
                                headersOf(
                                    HttpHeaders.ContentDisposition,
                                    ContentDisposition.Inline.withParameter(
                                        ContentDisposition
                                            .Parameters
                                            .Name,
                                        "senderUUID"
                                    )
                                        .toString()
                                )
                            )
                        )
                    )
                }
            ) {
                Assertions.assertThat(requestHandled).isTrue
                Assertions.assertThat(response.status()).isEqualTo(HttpStatusCode.OK)

                withHandle { handle ->

                    // DBの値をチェック
                    val senderInvoices = getSenderInvoices(handle)
                    Assertions.assertThat(senderInvoices.size).isEqualTo(1)
                    senderInvoices.first().let {
                        Assertions.assertThat(it.recipientUUID)
                            .isEqualTo(RecipientTableFixture().recipient_uuid)
                        Assertions.assertThat(it.senderUUID)
                            .isEqualTo(SenderTableFixture().sender_uuid)
                    }

                    // Storageにファイルが保存されていることを確認
                    Assertions.assertThat(
                        getStorageObject(
                            BlobId.of(
                                "sender-test-bucket",
                                "sender/${SenderTableFixture().sender_uuid}/sender-invoices/${senderInvoices.first().senderInvoiceUUID}.pdf"
                            )
                        )
                            .size
                    )
                        .isEqualTo(63341)

                    // ドメインイベントが発行されたことを確認
                    val domainEventRow =
                        DomainEventRepository.getByCallUUID(getCallUUID(), handle)
                    domainEventRow[0].apply {
                        Assertions.assertThat(this.domainEventName)
                            .isEqualTo(
                                "sender.domain.sender_invoice.SenderInvoiceRegistered"
                            )
                        JsonStringAssert.assertThat(this.message)
                            .jsonMatches(
                                """
                        {
                            "senderUUID" : "389afb90-42cd-4739-a1e1-0062a1d6285c",
                            "recipientUUID" : "015bdd8a-9aee-4545-8e4b-95b48a482559",
                            "senderInvoiceUUID" : "#uuid",
                            "senderSideInvoicePath" : {
                                "path" : "#string",
                                "bucket" : "sender-test-bucket"
                            }
                        }
                                """.trimIndent()
                            )
                    }
                }

                verify(exactly = 1) {
                    CloudTasksClient.create(any<CloudTasksSettings>())
                        .createTask(any<String>(), any())
                } // ドメインイベントのタスクが作られている
            }
        }

    private fun getSenderInvoices(handle: Handle): List<SenderInvoiceRow> {
        val sql =
            """
            SELECT
                sender_invoice_uuid,
                recipient_uuid,
                sender_uuid
            FROM sender_invoice
            """.trimIndent()

        return handle.createQuery(sql).mapTo(SenderInvoiceRow::class.java).list()
    }

    data class SenderInvoiceRow(
        val senderInvoiceUUID: UUID,
        val recipientUUID: UUID,
        val senderUUID: UUID
    )
}
