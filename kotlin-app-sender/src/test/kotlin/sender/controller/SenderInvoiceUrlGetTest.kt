package sender.controller

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import io.github.orangain.jsonmatch.JsonStringAssert
import io.ktor.http.*
import io.ktor.server.testing.*
import org.assertj.core.api.Assertions
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test
import sender.fixture.table.*
import sender.module
import sender.testing.Database
import sender.testing.testSettings
import sender.util.createStorageObject
import sender.util.deleteStorageObject
import java.util.*

class SenderInvoiceUrlGetTest {
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
        database.setUpTable(listOf(
            buildRecipientOperation(listOf(RecipientTableFixture(), RecipientTableFixture()._2人目())),
            buildSenderOperation(listOf(SenderTableFixture(), SenderTableFixture()._2人目())),
            buildSenderInvoiceOperation(listOf(
                SenderInvoiceTableFixture(),
            )),
        ))
    }

    @Test
    fun `請求書URLの取得`(): Unit = withTestApplication({ module(true, settings) }) {
        with(handleRequest(
            HttpMethod.Get,
            "/api/sender/sender/${SenderInvoiceTableFixture().sender_uuid}/sender-invoices/${SenderInvoiceTableFixture().sender_invoice_uuid}/pdf-url") {
        }) {
            Assertions.assertThat(response.status()).isEqualTo(HttpStatusCode.Found)
            Assertions.assertThat(response.headers.get("Location"))
                .startsWith("http://localhost:4443/sender-test-bucket/sender/${SenderInvoiceTableFixture().sender_uuid}/sender-invoices/${SenderInvoiceTableFixture().sender_invoice_uuid}.pdf")
        }
    }

}