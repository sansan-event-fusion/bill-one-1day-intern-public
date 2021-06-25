package recipient.controller

import io.github.orangain.jsonmatch.JsonStringAssert
import io.ktor.http.*
import io.ktor.server.testing.*
import org.assertj.core.api.Assertions
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test
import recipient.fixture.table.*
import recipient.module
import recipient.testing.Database
import recipient.testing.testSettings

class InvoicesGetTest {
    companion object {
        private val schemaName = this::class.java.declaringClass.simpleName.toLowerCase()
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
                buildSenderOperation(listOf(SenderTableFixture())),
                buildInvoiceOperation(listOf(InvoiceTableFixture())),
            )
        )
    }

    @Test
    fun `受領者の請求書一覧取得`(): Unit = withTestApplication({ module(true, settings) }) {

        with(handleRequest(
            HttpMethod.Get,
            "/api/recipient/recipient/${RecipientTableFixture().recipient_uuid}/invoices") {
        }) {
            Assertions.assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
            JsonStringAssert.assertThat(response.content).jsonMatches(
                """
                [ {
                  "invoiceUUID" : "cfc3a21f-6ac9-47ff-b76b-6cffad384d0a",
                  "recipientFullName" : "受領太郎",
                  "senderFullName" : "送付太郎",
                  "registeredAt" : "2019-01-01 09:00:00+09"
                } ]
            """.trimIndent()
            )
        }
    }
}
