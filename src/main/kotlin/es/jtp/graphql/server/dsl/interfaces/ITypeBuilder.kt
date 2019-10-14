package es.jtp.graphql.server.dsl.interfaces

import es.jtp.graphql.server.dsl.builders.*
import graphql.language.*

/**
 * Interface for type builders.
 */
interface ITypeBuilder {
    /**
     * Builds a [TypeDefinition].
     */
    fun build(context: GraphQLBuilderContext): TypeDefinition<*>

    /**
     * Prints the definition as a GraphQL schema.
     */
    fun toGraphQLString(): String
}
