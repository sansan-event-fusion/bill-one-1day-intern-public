package sender.util

import com.zaxxer.hikari.HikariDataSource
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.statement.SqlLogger
import org.jdbi.v3.core.statement.StatementContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sender.domain_event.Broker
import sender.domain_event.DomainEventContext
import sender.domain_event.DomainEventRepository
import sender.settings

val dataSource: HikariDataSource by lazy {
    val dataSource = HikariDataSource()
    dataSource.jdbcUrl = settings.jdbcUrl
    if (settings.environment == "test") {
        // テストを実行するとコネクションが足りなくなるので、テスト実行時はコネクションを補充しない。
        dataSource.minimumIdle = 0
    }
    dataSource
}

val jdbi: Jdbi by lazy {
    Jdbi.create(dataSource)
        .installPlugins()
        .setSqlLogger(CustomSqlLogger())
}

class CustomSqlLogger : SqlLogger {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun logBeforeExecution(context: StatementContext) {
        logger.debug(context.rawSql)
    }
}

inline fun <T> withHandle(block: (handle: Handle) -> T): T {
    return jdbi.open().use { h ->
        setSchemaForTenant(h)
        block(h)
    }
}

val runInTransactionLogger: Logger = LoggerFactory.getLogger("billone.tenant.util.runInTransaction")

// ドメインイベントのタスク作成漏れを防ぐため、トランザクション内でドメインイベントを発行しない場合でも常にdomainEventContextを要求する
inline fun <T> runInTransaction(domainEventContext: DomainEventContext, crossinline block: (handle: Handle) -> T): T {
    val result: T = jdbi.inTransaction<T, Exception> { h ->
        setSchemaForTenant(h)
        block(h)
    }

    // トランザクション終了後に、ドメインイベントがあればタスクを作成する。
    // トランザクションのコミット直前にタスクを作成することも検討したが、トランザクション内でAuth0など他のAPIを呼び出した後にタスク作成に失敗すると、不整合になるので採用しなかった。
    try {
        withHandle { handle ->
            val domainEvents =
                DomainEventRepository.getByCallUUID(domainEventContext.callUUID, handle).filter { !it.deployed }
            if (domainEvents.isNotEmpty()) {
                Broker().createTask(domainEventContext)
            }
        }
    } catch (ex: Exception) {
        // タスク作成中に例外が発生した場合、例外をログに出力するのみとして、リクエストは失敗させない。
        // リクエストを失敗させた場合、ユーザーから見ると、エラーが発生したのに変更は反映されている（=トランザクションがコミット済み）状態になって、違和感があるので。
        runInTransactionLogger.error(
            "ドメインイベントのタスク作成中に例外が発生しました。手動で再発行してください。 callUUID: ${domainEventContext.callUUID.value}",
            ex
        )
    }

    return result
}

fun setSchemaForTenant(h: Handle) {
    h.createUpdate("""SET search_path = "${settings.schema}"""") // search_pathはリテラルで指定するので、bindしない。
        .execute()
}
