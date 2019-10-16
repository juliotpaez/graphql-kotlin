package es.jtp.graphql.server.dsl.interfaces

import es.jtp.graphql.server.dsl.builders.*
import graphql.language.*
import graphql.schema.*

/**
 * Interface for type builders.
 */
interface IGraphQLBuilder {
    /**
     * Builds a [TypeDefinition].
     */
    fun build(context: GraphQLBuilderContext): GraphQLType
}
