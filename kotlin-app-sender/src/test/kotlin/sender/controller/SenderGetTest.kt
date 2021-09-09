package sender.controller

import io.github.orangain.jsonmatch.JsonStringAssert
import io.ktor.http.*
import io.ktor.server.testing.*
import org.assertj.core.api.Assertions
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test
import sender.fixture.table.SenderTableFixture
import sender.fixture.table.buildSenderOperation
import sender.module
import sender.testing.Database
import sender.testing.testSettings

class SenderGetTest {
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
                        buildSenderOperation(
                                listOf(SenderTableFixture(), SenderTableFixture()._2人目())
                        )
                )
        )
    }

    @Test
    fun `受領アカウント一覧の取得`(): Unit =
            withTestApplication({ module(true, settings) }) {
                with(handleRequest(HttpMethod.Get, "/api/sender/senders") {}) {
                    Assertions.assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
                    JsonStringAssert.assertThat(response.content)
                            .jsonMatches(
                                    """
                    [ {
                      "senderUUID" : "389afb90-42cd-4739-a1e1-0062a1d6285c",
                      "fullName" : "送付アカウント"
                    }, {
                      "senderUUID" : "553707cc-72d5-4634-b660-6f541102418a",
                      "fullName" : "送付アカウント2"
                    } ]
            """.trimIndent()
                            )
                }
            }
}
