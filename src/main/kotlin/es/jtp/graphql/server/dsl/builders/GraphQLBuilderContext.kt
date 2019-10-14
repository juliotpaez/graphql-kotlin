package es.jtp.graphql.server.dsl.builders

import graphql.schema.idl.*

/**
 * Context to build the a GraphQL schema.
 */
data class GraphQLBuilderContext(val runtimeWiringBuilder: RuntimeWiring.Builder)
