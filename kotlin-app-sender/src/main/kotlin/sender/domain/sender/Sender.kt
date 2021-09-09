package sender.domain.sender

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.util.*

data class Sender(
    val senderUUID: SenderUUID,
    val senderFullName: SenderFullName,
)

data class SenderUUID @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(@JsonValue val value: UUID)
data class SenderFullName @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(@JsonValue val value: String)
