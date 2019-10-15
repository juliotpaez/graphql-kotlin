package es.jtp.graphql.server.dsl.exceptions

import graphql.schema.*

/**
 * An exception occurred during the process of building a [GraphQLSchema].
 */
class GraphQLBuilderException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}
