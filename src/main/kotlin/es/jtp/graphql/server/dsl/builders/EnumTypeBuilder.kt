package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.exceptions.*
import es.jtp.graphql.server.dsl.interfaces.*
import graphql.schema.*
import kotlin.reflect.*


/**
 * Builder for a GraphQL object type.
 */
@Suppress("UNCHECKED_CAST")
class EnumTypeBuilder<T : Enum<T>>(val type: KClass<T>) : IGraphQLBuilder, IGraphQLWithDescription {
    override var description: String? = null
    internal val valueBuilders = mutableMapOf<String, EnumValueBuilder<T>>()

    init {
        for (constant in type.java.enumConstants) {
            val builder = EnumValueBuilder(constant)
            valueBuilders[constant.name] = builder
        }
    }

    // METHODS ----------------------------------------------------------------

    // TODO include directives

    /**
     * Modifies a field of a type.
     */
    fun value(enumValue: T, builderFn: EnumValueBuilder<T>.() -> Unit) {
        val definitionBuilder = valueBuilders[enumValue.name] ?: throw GraphQLBuilderException(
                "Cannot modify the field '${enumValue.name}'")
        builderFn.invoke(definitionBuilder)
    }

    /**
     * Builds a [GraphQLEnumType].
     */
    override fun build(context: GraphQLBuilderContext): GraphQLEnumType {
        val definition = GraphQLEnumType.newEnum()

        // Name
        definition.name(type.simpleName)

        // Description
        if (this.description != null) {
            definition.description(description)
        }

        // Values
        for (valueBuilder in valueBuilders) {
            val enumValue = valueBuilder.value.build(context)
            definition.value(enumValue)
        }

        return definition.build()
    }

    override fun toString(): String {
        return "Object(name=${type.simpleName}, description=$description, values=[${valueBuilders.values.joinToString(
                ", ")}]})"
    }
}
