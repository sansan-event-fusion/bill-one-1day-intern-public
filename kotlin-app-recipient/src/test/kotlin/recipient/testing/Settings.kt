package recipient.testing

import recipient.settingsFromEnv

// jdbcUrlをテストごとに上書きしても、コネクションプールは一度しか初期化しないので反映されないことに注意
fun testSettings(schemaName: String) = settingsFromEnv().copy(
    jdbcUrl = testJdbcUrl(),
    environment = "test",
    schema = schemaName
)

private fun testJdbcUrl() = System.getenv("JDBC_URL")
    ?: "jdbc:postgresql://localhost:5432/bill-one-1day-recipient-test?user=postgres"
