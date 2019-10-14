package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.utils.*
import graphql.language.*
import kotlin.reflect.*

/**
 * Builder for a GraphQL schema section.
 */
class SchemaBuilder {
    private val operations = mutableMapOf<String, String>()

    // METHODS ----------------------------------------------------------------

    // TODO include directives

    /**
     * Sets the name of the root type for queries.
     */
    inline fun <reified T : Any> query() = query(T::class)

    /**
     * Sets the root type for queries.
     */
    fun <T : Any> query(type: KClass<T>) {
        operations["query"] = type.simpleName!!
    }

    /**
     * Sets the name of the root type for mutations.
     */
    inline fun <reified T : Any> mutation() = mutation(T::class)

    /**
     * Sets the root type for mutations.
     */
    fun <T : Any> mutation(type: KClass<T>) {
        operations["mutation"] = type.simpleName!!
    }

    /**
     * Sets a custom root type using a type name.
     */
    inline fun <reified T : Any> rootOperation(name: String) = rootOperation(name, T::class)

    /**
     * Sets a custom root type using a type.
     */
    fun <T : Any> rootOperation(name: String, type: KClass<T>) {
        operations[name] = type.simpleName!!
    }

    /**
     * Builds a [SchemaDefinition].
     */
    fun build(context: GraphQLBuilderContext): SchemaDefinition {
        val definition = SchemaDefinition.newSchemaDefinition()

        // Operations
        for (nameType in operations) {
            val queryType = TypeName.newTypeName(nameType.value).build()
            val queryOperation =
                    OperationTypeDefinition.newOperationTypeDefinition().name(nameType.key).typeName(queryType).build()

            definition.operationTypeDefinition(queryOperation)
        }

        return definition.build()
    }

    /**
     * Prints the definition as a GraphQL schema.
     */
    fun toGraphQLString() = StringBuilder().apply {
        append("schema {\n")

        for (operation in operations) {
            append(PrinterUtils.indent("${operation.key}: ${operation.value}"))
            append("\n")
        }

        append("}")
    }.toString()

    override fun toString(): String {
        return "Schema(operations=[${operations.toList().joinToString(", ") { "${it.first}: ${it.second}" }}])"
    }
}
