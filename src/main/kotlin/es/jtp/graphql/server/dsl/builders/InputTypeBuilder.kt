package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.exceptions.*
import es.jtp.graphql.server.dsl.interfaces.*
import graphql.schema.*
import kotlin.reflect.*
import kotlin.reflect.full.*


/**
 * Builder for a GraphQL input type.
 */
class InputTypeBuilder<T : Any>(val type: KClass<T>) : IGraphQLBuilder, IGraphQLWithDescription {
    override var description: String? = null
    internal val fieldBuilder = mutableMapOf<KProperty1<T, *>, InputFieldBuilder<T, *>>()

    init {
        for (field in type.memberProperties) {
            val builder = InputFieldBuilder(field)
            fieldBuilder[field] = builder
        }
    }

    // METHODS ----------------------------------------------------------------

    // TODO include directives

    /**
     * Modifies a field of an input type.
     */
    @Suppress("UNCHECKED_CAST")
    fun <V> field(fieldProperty: KProperty1<T, V>, builderFn: InputFieldBuilder<T, V>.() -> Unit) {
        val definitionBuilder = fieldBuilder[fieldProperty] ?: throw GraphQLBuilderException(
                "Cannot modify the field '${fieldProperty.name}'")
        builderFn.invoke(definitionBuilder as InputFieldBuilder<T, V>)
    }

    /**
     * Builds a [GraphQLInputObjectType].
     */
    override fun build(context: GraphQLBuilderContext): GraphQLInputObjectType {
        val definition = GraphQLInputObjectType.newInputObject()

        // Name
        definition.name(type.simpleName)

        // Description
        if (this.description != null) {
            definition.description(description)
        }

        // Fields
        for (definitionBuilder in fieldBuilder.values) {
            val fieldDefinition = definitionBuilder.build(context)
            definition.field(fieldDefinition)
        }

        return definition.build()
    }

    override fun toString(): String {
        return "Object(name=${type.simpleName}, description=$description, fields=[${fieldBuilder.values.joinToString(
                ", ")}])"
    }
}
