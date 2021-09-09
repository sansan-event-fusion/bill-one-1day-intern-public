package recipient

import recipient.testing.testSettings
import io.ktor.http.*
import io.ktor.server.testing.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.AfterClass
import org.junit.Before
import org.junit.Test
import recipient.testing.Database

class ApplicationTest {
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
        database.setUpTable(emptyList())
    }

    @Test
    fun testIndex() = testApp {
        handleRequest(HttpMethod.Get, "/") {
        }.apply {
            assertThat(response.status()?.value).isEqualTo(200)
            assertThat(response.content!!).isEqualTo("bill-one-1day-intern kotlin-app-recipient is up!")
        }
    }

    private fun testApp(callback: TestApplicationEngine.() -> Unit) {
        withTestApplication({ module(true, settings) }) { callback() }
    }
}
