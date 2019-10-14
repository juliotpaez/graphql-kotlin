package es.jtp.graphql.server.dsl.exceptions

/**
 * An exception that is thrown when an unimplemented path is reached during the process of building a GraphQL.
 */
class GraphQLBuilderUnimplementedException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}
