package recipient.fixture.table

import com.ninja_squad.dbsetup.operation.Operation
import recipient.testing.buildLoadingFixturesOperation
import java.util.UUID

data class InvoiceMemoTableFixture(
    val invoice_uuid: UUID = InvoiceTableFixture().invoice_uuid,
    val memo: String = "test memo",
)

fun buildInvoiceMemoOperation(fixtureAccounts: List<InvoiceMemoTableFixture>): Operation {
    return buildLoadingFixturesOperation(fixtureAccounts, "invoice_memo")
}
