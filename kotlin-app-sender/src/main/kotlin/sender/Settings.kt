package sender

data class Settings(
    val jdbcUrl: String,
    val schema: String = "sender",
    val environment: String = "dev",
    val recipientApiUrl: String = "http://localhost:8081",
    val senderApiUrl: String = "http://localhost:8082",
    val senderServiceQueryPath: String = "projects/bill-one-1day-intern/locations/asia-northeast1/queues/sender",
    val serviceAccount: String = ""
) {
    val senderInvoiceBucket: String = "sender-$environment-bucket"
    val cloudTasksEmulatorHost: String? = if (environment == "dev") "localhost:9090" else null
    val cloudStorageEmulatorHost: String? = if (environment == "dev" || environment == "test") "http://localhost:4443" else null
}

fun settingsFromEnv() = Settings(
    jdbcUrl = "jdbc:postgresql://localhost:5432/bill-one-1day-sender?user=postgres"
)
