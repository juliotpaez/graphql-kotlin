package es.jtp.graphql.server.dsl

import es.jtp.graphql.server.dsl.builders.*
import graphql.schema.*

/**
 * Generates a new [GraphQLSchema].
 */
fun graphQL(builderFn: GraphQLSchemaBuilder.() -> Unit): GraphQLSchema {
    val builder = GraphQLSchemaBuilder()
    builderFn(builder)
    return builder.build()
}
