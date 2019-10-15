package es.jtp.graphql.server.dsl.builders

import graphql.schema.*

/**
 * Context to build the a GraphQL schema.
 */
data class GraphQLBuilderContext(val schema: GraphQLSchema.Builder, val codeRegistry: GraphQLCodeRegistry.Builder,
        val types: MutableMap<String, GraphQLType> = mutableMapOf())
