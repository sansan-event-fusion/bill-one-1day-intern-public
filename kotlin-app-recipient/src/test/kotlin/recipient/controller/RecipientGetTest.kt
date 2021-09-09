package recipient.controller

import io.github.orangain.jsonmatch.JsonStringAssert
import io.ktor.http.*
import io.ktor.server.testing.*
import org.assertj.core.api.Assertions
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test
import recipient.fixture.table.RecipientTableFixture
import recipient.fixture.table.buildRecipientOperation
import recipient.module
import recipient.testing.Database
import recipient.testing.testSettings

class RecipientGetTest {
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
                        buildRecipientOperation(
                                listOf(RecipientTableFixture(), RecipientTableFixture()._2人目())
                        ),
                )
        )
    }

    @Test
    fun `受領アカウント一覧の取得`(): Unit =
            withTestApplication({ module(true, settings) }) {
                with(handleRequest(HttpMethod.Get, "/api/recipient/recipients") {}) {
                    Assertions.assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                    JsonStringAssert.assertThat(response.content)
                            .jsonMatches(
                                    """
                [ {
                  "recipientUUID" : "015bdd8a-9aee-4545-8e4b-95b48a482559",
                  "fullName" : "受領アカウント1"
                }, {
                  "recipientUUID" : "c60fb756-31f5-4e38-9c50-f59f5c8091a8",
                  "fullName" : "受領アカウント2"
                } ]
            """.trimIndent()
                            )
                }
            }
}
