package sender.util

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.util.*

data class CallUUID @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(@JsonValue val value: UUID)
