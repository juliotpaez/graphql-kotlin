package es.jtp.graphql.server.dsl.utils

import graphql.*
import graphql.schema.*
import java.lang.reflect.*
import kotlin.reflect.*
import kotlin.reflect.jvm.*

/**
 * Gets the declaring type of a [KProperty1]
 */
fun KProperty1<*, *>.declaringClass(): Class<*> {
    return (this.javaClass as? Member ?: this.javaGetter)?.declaringClass ?: error("Unable to access declaring class")
}

/**
 * Builds a [GraphQL] from a [GraphQLSchema].
 */
fun GraphQLSchema.build() = GraphQL.newGraphQL(this).build()!!
