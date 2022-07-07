package recipient.domain.recipient

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.util.*

data class Recipient(
    val recipientUUID: RecipientUUID,
    val recipientFullName: RecipientFullName
)

data class RecipientUUID @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(@JsonValue val value: UUID)
data class RecipientFullName @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(@JsonValue val value: String)
