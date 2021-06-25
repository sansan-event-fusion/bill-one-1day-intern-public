package sender.testing

import com.ninja_squad.dbsetup.Operations
import com.ninja_squad.dbsetup.operation.Operation
import kotlin.reflect.full.memberProperties


fun <T : Any> buildLoadingFixturesOperation(fixtures: List<T>, tableName: String): Operation {

    val operations = Operations.insertInto(tableName)

    fixtures.forEach { fixture ->
        val row = operations.row()
        fixture::class.memberProperties.forEach {
            row.column(it.name, it.call(fixture))
        }

        row.end()
    }

    return operations.build()
}
