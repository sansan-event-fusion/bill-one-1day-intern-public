package recipient.fixture.table

import com.ninja_squad.dbsetup.operation.Operation
import recipient.testing.buildLoadingFixturesOperation
import java.time.OffsetDateTime
import java.util.*

data class RecipientTableFixture(
    val recipient_uuid: UUID = UUID.fromString("015bdd8a-9aee-4545-8e4b-95b48a482559"),
    val full_name: String = "受領アカウント1",
    val created_at: OffsetDateTime = OffsetDateTime.parse("2019-01-01T00:00:00Z")
) {
    fun _2人目() =
        copy(
            recipient_uuid =
            UUID.fromString(
                "c60fb756-31f5-4e38-9c50-f59f5c8091a8"
            ),
            full_name = "受領アカウント2"
        )
}

fun buildRecipientOperation(fixtureAccounts: List<RecipientTableFixture>): Operation {
    return buildLoadingFixturesOperation(fixtureAccounts, "recipient")
}
