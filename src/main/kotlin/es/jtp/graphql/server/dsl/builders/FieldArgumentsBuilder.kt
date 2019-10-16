package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.exceptions.*
import kotlin.reflect.*
import kotlin.reflect.full.*

/**
 * Builder for a GraphQL field arguments.
 */
class FieldArgumentsBuilder<T : Any>(val type: KClass<T>) {
    internal val arguments = mutableMapOf<KProperty1<T, *>, ArgumentBuilder<T, *>>()

    init {
        // Add arguments
        for (field in type.memberProperties) {
            val builder = ArgumentBuilder(field)
            arguments[field] = builder
        }
    }
    // METHODS ----------------------------------------------------------------

    /**
     * Modifies an argument.
     */
    @Suppress("UNCHECKED_CAST")
    fun <V> argument(fieldProperty: KProperty1<T, V>, builderFn: ArgumentBuilder<T, V>.() -> Unit) {
        val definitionBuilder = arguments[fieldProperty] ?: throw GraphQLBuilderException(
                "Cannot modify the argument '${fieldProperty.name}'")
        builderFn.invoke(definitionBuilder as ArgumentBuilder<T, V>)
    }

    override fun toString(): String {
        return "FieldArguments(arguments=$arguments)"
    }
}
