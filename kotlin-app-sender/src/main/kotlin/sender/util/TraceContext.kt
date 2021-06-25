package sender.util

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

data class TraceContext @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(@JsonValue val value: String) {
    private val pieces by lazy {
        value.split("/", ";o=", limit = 3)
    }

    fun traceId(): String = pieces[0]
    fun spanId(): String = if (pieces.size >= 2) pieces[1] else "0" // 稀に"/"が含まれていないことがあるので、それでも例外を起こさないようにする
    fun traceSampled(): Boolean = pieces.size >= 3 && pieces[2] == "1"
}
