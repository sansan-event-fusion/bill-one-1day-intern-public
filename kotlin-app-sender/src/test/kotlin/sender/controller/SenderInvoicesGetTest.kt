package sender.controller

import io.github.orangain.jsonmatch.JsonStringAssert
import io.ktor.http.*
import io.ktor.server.testing.*
import org.assertj.core.api.Assertions
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test
import sender.fixture.table.*
import sender.module
import sender.testing.*
import java.util.*

class SenderInvoicesGetTest {
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
                buildRecipientOperation(
                    listOf(
                        RecipientTableFixture(),
                        RecipientTableFixture()
                            ._2人目()
                    )
                ),
                buildSenderOperation(
                    listOf(
                        SenderTableFixture(),
                        SenderTableFixture()
                            ._2人目()
                    )
                ),
                buildSenderInvoiceOperation(
                    listOf(
                        SenderInvoiceTableFixture(),
                        SenderInvoiceTableFixture()
                            ._2枚目(),
                        SenderInvoiceTableFixture()
                            .copy(
                                sender_invoice_uuid =
                                UUID
                                    .randomUUID(),
                                sender_uuid =
                                SenderTableFixture()
                                    ._2人目()
                                    .sender_uuid
                            )
                    )
                ),
                buildSenderInvoiceMemoOperation(
                    listOf(
                        SenderInvoiceMemoTableFixture(),
                        SenderInvoiceMemoTableFixture()._2枚目()
                    )
                )
            )
        )
    }

    @Test
    fun `送付済み請求書一覧の取得`(): Unit =
        withTestApplication({ module(true, settings) }) {
            with(
                handleRequest(
                    HttpMethod.Get,
                    "/api/sender/sender/${SenderTableFixture().sender_uuid}/sender-invoices"
                ) {}
            ) {
                Assertions.assertThat(response.status())
                    .isEqualTo(HttpStatusCode.OK)
                JsonStringAssert.assertThat(response.content)
                    .jsonMatches(
                        """
                [ {
                  "senderInvoiceUUID" : "a0a678d9-9fdd-4acb-9d89-e2345c69a016",
                  "recipientFullName" : "受領アカウント1",
                  "senderFullName" : "送付アカウント",
                  "registeredAt" : "2019-01-01 09:00:00+09",
                  "memo" : "test memo"
                }, {
                  "senderInvoiceUUID" : "8c9f66c5-0308-4f12-a444-dcf4836d50c9",
                  "recipientFullName" : "受領アカウント2",
                  "senderFullName" : "送付アカウント",
                  "registeredAt" : "2019-01-01 09:00:00+09",
                  "memo" : "test memo 2"
                } ]
                        """.trimIndent()
                    )
            }
        }

    @Test
    fun `もう1人の送付済み請求書一覧の取得`(): Unit =
        withTestApplication({ module(true, settings) }) {
            with(
                handleRequest(
                    HttpMethod.Get,
                    "/api/sender/sender/${SenderTableFixture()._2人目().sender_uuid}/sender-invoices"
                ) {}
            ) {
                Assertions.assertThat(response.status())
                    .isEqualTo(HttpStatusCode.OK)
                JsonStringAssert.assertThat(response.content)
                    .jsonMatches(
                        """
                [ {
                  "senderInvoiceUUID" : "#uuid",
                  "recipientFullName" : "受領アカウント1",
                  "senderFullName" : "送付アカウント2",
                  "registeredAt" : "2019-01-01 09:00:00+09",
                  "memo" : null
                } ]
                        """.trimIndent()
                    )
            }
        }
}
