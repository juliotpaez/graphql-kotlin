package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.exceptions.*
import graphql.*
import graphql.schema.*
import kotlin.reflect.*

/**
 * Builder for a GraphQL schema section.
 */
class SchemaBuilder {
    private var query: KClass<*>? = null
    private var mutation: KClass<*>? = null
    private var subscription: KClass<*>? = null

    // METHODS ----------------------------------------------------------------

    // TODO include directives

    /**
     * Sets the name of the root type for queries.
     */
    inline fun <reified T : Any> query() = query(T::class)

    /**
     * Sets the root type for queries.
     */
    fun <T : Any> query(type: KClass<T>) {
        query = type
    }

    /**
     * Sets the name of the root type for mutations.
     */
    inline fun <reified T : Any> mutation() = mutation(T::class)

    /**
     * Sets the root type for mutations.
     */
    fun <T : Any> mutation(type: KClass<T>) {
        mutation = type
    }

    /**
     * Sets the name of the root type for subscriptions.
     */
    inline fun <reified T : Any> subscription() = subscription(T::class)

    /**
     * Sets the root type for subscriptions.
     */
    fun <T : Any> subscription(type: KClass<T>) {
        mutation = type
    }

    //    /**
    //     * Sets a custom root type using a type name.
    //     */
    //    inline fun <reified T : Any> rootOperation(name: String) = rootOperation(name, T::class)
    //
    //    /**
    //     * Sets a custom root type using a type.
    //     */
    //    fun <T : Any> rootOperation(name: String, type: KClass<T>) {
    //        operations[name] = type.simpleName!!
    //    }

    /**
     * Builds a [GraphQLSchema].
     */
    fun build(context: GraphQLBuilderContext): GraphQLSchema {
        val definition = context.schema

        // Operations
        if (query != null) {
            val type = context.types[query!!.simpleName] ?: throw GraphQLBuilderException(
                    "There is no type defined with name '${query!!.simpleName}'")

            type as? GraphQLObjectType ?: throw GraphQLBuilderException(
                    "Query requires that the '${query!!.simpleName}' type is an ObjectType")

            definition.query(type)
        }

        if (mutation != null) {
            val type = context.types[query!!.simpleName] ?: throw GraphQLBuilderException(
                    "There is no type defined with name '${query!!.simpleName}'")

            type as? GraphQLObjectType ?: throw GraphQLBuilderException(
                    "Mutation requires that the '${query!!.simpleName}' type is an ObjectType")

            definition.mutation(type)
        }

        if (subscription != null) {
            val type = context.types[query!!.simpleName] ?: throw GraphQLBuilderException(
                    "There is no type defined with name '${query!!.simpleName}'")

            type as? GraphQLObjectType ?: throw GraphQLBuilderException(
                    "Subscription requires that the '${query!!.simpleName}' type is an ObjectType")

            definition.subscription(type)
        }

        // Put defaults.
        let {
            definition.additionalType(Scalars.GraphQLBoolean)
            definition.additionalType(Scalars.GraphQLByte)
            definition.additionalType(Scalars.GraphQLShort)
            definition.additionalType(Scalars.GraphQLInt)
            definition.additionalType(Scalars.GraphQLLong)
            definition.additionalType(Scalars.GraphQLBigInteger)
            definition.additionalType(Scalars.GraphQLFloat)
            definition.additionalType(Scalars.GraphQLBigDecimal)
            definition.additionalType(Scalars.GraphQLChar)
            definition.additionalType(Scalars.GraphQLString)
            definition.additionalType(Scalars.GraphQLID)
        }

        val codeRegistry = context.codeRegistry.build()
        definition.codeRegistry(codeRegistry)
        return definition.build()
    }

    override fun toString(): String {
        return "SchemaBuilder(query=$query, mutation=$mutation, subscription=$subscription)"
    }
}
