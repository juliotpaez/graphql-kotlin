package es.jtp.graphql.server.dsl.exceptions

import graphql.schema.*

/**
 * An exception that is thrown when an unimplemented path is reached during the process of building a [GraphQLSchema].
 */
class GraphQLBuilderUnimplementedException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}
