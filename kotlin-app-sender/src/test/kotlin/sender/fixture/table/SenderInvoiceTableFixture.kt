package sender.fixture.table

import com.ninja_squad.dbsetup.operation.Operation
import sender.testing.buildLoadingFixturesOperation
import java.time.OffsetDateTime
import java.util.*

data class SenderInvoiceTableFixture(
    val sender_invoice_uuid: UUID = UUID.fromString("a0a678d9-9fdd-4acb-9d89-e2345c69a016"),
    val recipient_uuid: UUID = RecipientTableFixture().recipient_uuid,
    val sender_uuid: UUID = SenderTableFixture().sender_uuid,
    val registered_at: OffsetDateTime = OffsetDateTime.parse("2019-01-01T00:00:00Z"),
    val created_at: OffsetDateTime = OffsetDateTime.parse("2019-01-01T00:00:00Z"),
) {
    fun _2枚目() = copy(
        sender_invoice_uuid = UUID.fromString("8c9f66c5-0308-4f12-a444-dcf4836d50c9"),
        recipient_uuid = RecipientTableFixture()._2人目().recipient_uuid
    )
}

fun buildSenderInvoiceOperation(fixtureAccounts: List<SenderInvoiceTableFixture>): Operation {
    return buildLoadingFixturesOperation(fixtureAccounts, "sender_invoice")
}
