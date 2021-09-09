package recipient.fixture.table

import com.ninja_squad.dbsetup.operation.Operation
import recipient.testing.buildLoadingFixturesOperation
import java.time.OffsetDateTime
import java.util.*

data class InvoiceTableFixture(
    val invoice_uuid: UUID = UUID.fromString("cfc3a21f-6ac9-47ff-b76b-6cffad384d0a"),
    val sender_invoice_uuid: UUID = UUID.fromString("389afb90-42cd-4739-a1e1-062a1d6285cc"),
    val recipient_uuid: UUID = RecipientTableFixture().recipient_uuid,
    val sender_uuid: UUID = SenderTableFixture().sender_uuid,
    val registered_at: OffsetDateTime = OffsetDateTime.parse("2019-01-01T00:00:00Z"),
    val created_at: OffsetDateTime = OffsetDateTime.parse("2019-01-01T00:00:00Z"),
)

fun buildInvoiceOperation(fixtureAccounts: List<InvoiceTableFixture>): Operation {
    return buildLoadingFixturesOperation(fixtureAccounts, "invoice")
}
