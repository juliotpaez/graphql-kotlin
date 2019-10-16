package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.interfaces.*
import graphql.schema.*
import kotlin.reflect.*

/**
 * Builder for a GraphQL argument.
 */
class ArgumentBuilder<T : Any, V>(val field: KProperty1<T, V>) : IGraphQLWithDescription {
    override var description: String? = null
    private var defaultValue: V? = null

    // METHODS ----------------------------------------------------------------

    // TODO include directives - add deprecated

    /**
     * Sets the default value.
     */
    fun defaultValue(value: V) {
        this.defaultValue = value
    }

    /**
     * Builds a [GraphQLArgument].
     */
    fun build(context: GraphQLBuilderContext): GraphQLArgument {
        val definition = GraphQLArgument.newArgument()

        // Name
        definition.name(field.name)

        // Type
        val typeClass = field.returnType.classifier as KClass<*>
        definition.type(GraphQLTypeReference.typeRef(typeClass.simpleName))

        // Description
        if (this.description != null) {
            definition.description(description)
        }

        // Default value
        if (this.defaultValue != null) {
            definition.defaultValue(this.defaultValue)
        }

        return definition.build()
    }

    override fun toString(): String {
        return "Argument(name=${field.name}, type=${(field.returnType.classifier as KClass<*>).simpleName}, description=$description, defaultValue=$defaultValue)"
    }
}
