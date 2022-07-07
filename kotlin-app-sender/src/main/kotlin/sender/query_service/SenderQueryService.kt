package sender.query_service

import sender.util.withHandle
import java.util.*

object SenderQueryService {
    fun getAll(): List<SenderGetQueryResult> {
        val sql = """
            SELECT
                sender_uuid,
                full_name
            FROM sender
        """.trimIndent()

        withHandle { handle ->
            return handle.createQuery(sql)
                .mapTo(SenderGetQueryResult::class.java)
                .list()
        }
    }
}

data class SenderGetQueryResult(
    val senderUUID: UUID,
    val fullName: String
)
