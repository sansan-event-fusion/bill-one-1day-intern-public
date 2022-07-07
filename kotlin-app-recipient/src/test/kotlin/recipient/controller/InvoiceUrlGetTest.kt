package recipient.controller

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

class SenderInvoiceUrlGetTest {
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
                buildRecipientOperation(listOf(RecipientTableFixture(), RecipientTableFixture()._2人目())),
                buildSenderOperation(listOf(SenderTableFixture(), SenderTableFixture()._2人目())),
                buildInvoiceOperation(
                    listOf(
                        InvoiceTableFixture()
                    )
                )
            )
        )
    }

    @Test
    fun `請求書URLの取得`(): Unit = withTestApplication({ module(true, settings) }) {
        with(
            handleRequest(
                HttpMethod.Get,
                "/api/recipient/recipient/${InvoiceTableFixture().recipient_uuid}/invoices/${InvoiceTableFixture().invoice_uuid}/pdf-url"
            ) {
            }
        ) {
            Assertions.assertThat(response.status()).isEqualTo(HttpStatusCode.Found)
            Assertions.assertThat(response.headers.get("Location"))
                .startsWith("http://localhost:4443/recipient-test-bucket/recipient/${InvoiceTableFixture().recipient_uuid}/invoices/${InvoiceTableFixture().invoice_uuid}.pdf")
        }
    }
}
