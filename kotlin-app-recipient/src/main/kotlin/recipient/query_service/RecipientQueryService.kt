package recipient.query_service

import recipient.util.withHandle
import java.util.*

object RecipientQueryService {
    fun getAll(): List<RecipientGetQueryResult> {
        val sql = """
            SELECT
                recipient_uuid,
                full_name
            FROM recipient
        """.trimIndent()

        withHandle { handle ->
            return handle.createQuery(sql)
                .mapTo(RecipientGetQueryResult::class.java)
                .list()
        }
    }
}

data class RecipientGetQueryResult(
    val recipientUUID: UUID,
    val fullName: String
)
