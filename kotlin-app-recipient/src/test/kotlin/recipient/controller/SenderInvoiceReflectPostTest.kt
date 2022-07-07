package recipient.controller

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import io.ktor.http.*
import io.ktor.server.testing.*
import org.assertj.core.api.Assertions
import org.jdbi.v3.core.Handle
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test
import recipient.fixture.table.RecipientTableFixture
import recipient.fixture.table.SenderTableFixture
import recipient.fixture.table.buildRecipientOperation
import recipient.fixture.table.buildSenderOperation
import recipient.module
import recipient.testing.Database
import recipient.testing.testSettings
import recipient.util.createStorageObject
import recipient.util.getStorageObject
import recipient.util.withHandle
import java.util.*

class SenderInvoiceReflectPostTest {
    companion object {
        private val schemaName = this::class.java.declaringClass.simpleName.lowercase()
        private val settings = testSettings(schemaName)
        private val database = Database(settings, schemaName)

        private val pdf = this::class.java.classLoader.getResource("pdf/upload.pdf")!!.readBytes()
        private val senderInvoiceUUID = UUID.randomUUID()
        private val senderInvoiceBlobId =
            BlobId.of(
                "sender-test-bucket",
                "sender/${SenderTableFixture().sender_uuid}/sender-invoices/$senderInvoiceUUID.pdf"
            )
        private val senderInvoiceBlobInfo =
            BlobInfo.newBuilder(senderInvoiceBlobId).setContentType("application/pdf").build()

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
    }

    @Test
    fun `送付アカウントからの請求書を受領`(): Unit =
        withTestApplication({ module(true, settings) }) {
            createStorageObject(senderInvoiceBlobInfo, pdf)

            val requestJson =
                """
            {
                "senderUUID" : "${SenderTableFixture().sender_uuid}",
                "recipientUUID" : "${RecipientTableFixture().recipient_uuid}",
                "senderInvoiceUUID" : "$senderInvoiceUUID",
                "senderSideInvoicePath" : {
                    "path" : "${senderInvoiceBlobId.name}",
                    "bucket" : "${senderInvoiceBlobId.bucket}"
                }
            }
                """.trimIndent()

            with(
                handleRequest(
                    HttpMethod.Post,
                    "/event-handler/sender/reflect-sender-invoice"
                ) {
                    addHeader(HttpHeaders.Accept, ContentType.Text.Plain.toString())
                    addHeader(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString()
                    )
                    setBody(requestJson)
                }
            ) {
                Assertions.assertThat(response.status()).isEqualTo(HttpStatusCode.OK)

                withHandle { handle ->
                    // DBの値をチェック
                    val invoices = getInvoices(handle)
                    Assertions.assertThat(invoices.size).isEqualTo(1)
                    invoices.first().let {
                        Assertions.assertThat(it.recipientUUID)
                            .isEqualTo(RecipientTableFixture().recipient_uuid)
                        Assertions.assertThat(it.senderUUID)
                            .isEqualTo(SenderTableFixture().sender_uuid)
                        Assertions.assertThat(it.senderInvoiceUUID).isEqualTo(senderInvoiceUUID)
                    }

                    // Storageにファイルが保存されていることを確認
                    Assertions.assertThat(
                        getStorageObject(
                            BlobId.of(
                                "recipient-test-bucket",
                                "recipient/${RecipientTableFixture().recipient_uuid}/invoices/${invoices.first().invoiceUUID}.pdf"
                            )
                        )
                            .size
                    )
                        .isEqualTo(63341)
                }
            }
        }

    private fun getInvoices(handle: Handle): List<InvoiceRow> {
        val sql =
            """
            SELECT
                invoice_uuid,
                sender_invoice_uuid,
                recipient_uuid,
                sender_uuid
            FROM invoice
            """.trimIndent()

        return handle.createQuery(sql).mapTo(InvoiceRow::class.java).list()
    }

    data class InvoiceRow(
        val invoiceUUID: UUID,
        val senderInvoiceUUID: UUID,
        val recipientUUID: UUID,
        val senderUUID: UUID
    )
}
