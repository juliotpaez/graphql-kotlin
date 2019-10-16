package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.interfaces.*
import graphql.schema.*

/**
 * Builder for a GraphQL field definition.
 */
class EnumValueBuilder<T : Enum<T>>(val enumValue: T) : IGraphQLWithDescription {
    override var description: String? = null

    // METHODS ----------------------------------------------------------------

    // TODO include directives - add deprecated

    /**
     * Builds a [GraphQLEnumValueDefinition].
     */
    internal fun build(context: GraphQLBuilderContext): GraphQLEnumValueDefinition? {
        val definition = GraphQLEnumValueDefinition.newEnumValueDefinition()

        // Name
        definition.name(enumValue.name)

        // Description
        if (this.description != null) {
            definition.description(description)
        }

        return definition.build()
    }

    override fun toString(): String {
        return "Field(name=${enumValue.name}, description=$description)"
    }
}
