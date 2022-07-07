package recipient.testing

import com.ninja_squad.dbsetup.DbSetup
import com.ninja_squad.dbsetup.Operations
import com.ninja_squad.dbsetup.destination.DriverManagerDestination
import com.ninja_squad.dbsetup.operation.Operation
import recipient.Settings
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

private val migrationSQL: String by lazy {
    val migrationSqlPaths =
        Files.list(Path.of("src/main/resources/db/migration")).filter { it.toString().endsWith(".sql") }.sorted()
            .toList()
    check(
        migrationSqlPaths.map { it.fileName.toString().split("__")[0] }
            .distinct().size == migrationSqlPaths.size
    ) { "マイグレーションファイルのバージョン番号が重複しています。 " }
    migrationSqlPaths.joinToString(";\n") { Files.readString(it) } // SQLファイルの中身を全て結合する。
}

class Database(private val settings: Settings, private val schema: String) {

    fun setUpTable(operations: List<Operation>) {
        // CREATE SCHEMA and TABLE
        val dest = DriverManagerDestination(settings.jdbcUrl, null, null)
        dest.connection.autoCommit = false

        val migrationOperation = Operations.sql( // オペレーションを分割すると遅くなるので、できるだけ1つのSQLにまとめる。
            """
                DROP SCHEMA IF EXISTS "$schema" CASCADE;
                CREATE SCHEMA "$schema";
                SET search_path = "$schema";
                $migrationSQL
            """.trimIndent()
        )

        val ops = Operations.sequenceOf(
            migrationOperation,
            Operations.sequenceOf(operations)
        )
        val dbSetup = DbSetup(dest, ops)
        dbSetup.launch()
    }

    fun tearDown() {
        val dest = DriverManagerDestination(settings.jdbcUrl, null, null)
        val dropSchema = Operations.sql("""DROP SCHEMA IF EXISTS "$schema" CASCADE""")
        val ops = Operations.sequenceOf(dropSchema)
        val dbSetup = DbSetup(dest, ops)
        dbSetup.launch()
    }
}

fun tenantNameIdPrefix(): String {
    val commitId = System.getenv("COMMIT_ID") ?: return ""
    return "$commitId-"
}
