package es.jtp.graphql.server.dsl

import es.jtp.graphql.server.dsl.builders.*
import graphql.*
import graphql.schema.idl.*

/**
 * Generates a new [GraphQL].
 */
fun graphQL(builderFn: GraphQLBuilder.() -> Unit): GraphQL {
    val builder = GraphQLBuilder()
    builderFn(builder)

    val runtimeWiringBuilder = RuntimeWiring.newRuntimeWiring()
    val context = GraphQLBuilderContext(runtimeWiringBuilder)
    val typeRegistry = builder.build(context)
    val runtimeWiring = runtimeWiringBuilder.build()
    val graphQLSchema = SchemaGenerator().makeExecutableSchema(typeRegistry, runtimeWiring)
    val graphQLBuilder = GraphQL.newGraphQL(graphQLSchema)

    return graphQLBuilder.build()!!
}
