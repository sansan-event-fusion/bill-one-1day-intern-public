package sender.fixture.table

import com.ninja_squad.dbsetup.operation.Operation
import sender.testing.buildLoadingFixturesOperation
import java.util.UUID

data class SenderInvoiceMemoTableFixture(
    val sender_invoice_uuid: UUID = SenderInvoiceTableFixture().sender_invoice_uuid,
    val memo:String = "test memo",
){
    fun _2枚目() = copy(
        sender_invoice_uuid = SenderInvoiceTableFixture()._2枚目().sender_invoice_uuid,
        memo = "test memo 2"
    )
}

fun buildSenderInvoiceMemoOperation(fixtureAccounts: List<SenderInvoiceMemoTableFixture>): Operation {
    return buildLoadingFixturesOperation(fixtureAccounts, "sender_invoice_memo")
}