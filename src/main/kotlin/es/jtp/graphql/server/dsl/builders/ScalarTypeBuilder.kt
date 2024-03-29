package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.exceptions.*
import es.jtp.graphql.server.dsl.interfaces.*
import es.jtp.graphql.server.dsl.utils.*
import graphql.language.*
import graphql.schema.*
import kotlin.reflect.*

/**
 * Builder for a GraphQL scalar type.
 */
class ScalarTypeBuilder<I : Any>(internal val type: KClass<I>) : IGraphQLBuilder, IGraphQLWithDescription {
    override var description: String? = null
    private var serializer: ((I) -> Any?)? = null
    private var deserializer: ((Any?) -> I)? = null

    // METHODS ----------------------------------------------------------------

    // TODO include directives

    /**
     * Sets a function that takes a Runtime object and converts it into the output scalar.
     */
    fun serializer(fn: (I) -> Any?) {
        this.serializer = fn
    }

    /**
     * Sets a function that takes a scalar and converts it into the Runtime object.
     */
    fun deserializer(fn: (Any?) -> I) {
        this.deserializer = fn
    }

    /**
     * Builds a [GraphQLScalarType].
     */
    override fun build(context: GraphQLBuilderContext): GraphQLScalarType {
        val definition = GraphQLScalarType.newScalar()

        // Name
        definition.name(type.simpleName)

        // Description
        if (this.description != null) {
            definition.description(this.description!!)
        }

        // (De)serializer
        val serializer = this.serializer ?: throw GraphQLBuilderException(
                "The serialize method must be defined for scalar type ${type.simpleName}.")
        val deserializer = this.deserializer ?: throw GraphQLBuilderException(
                "The deserialize method must be defined for scalar type ${type.simpleName}.")

        val coercing = @Suppress("UNCHECKED_CAST") object : Coercing<I, Any> {
            override fun serialize(input: Any): Any? {
                try {
                    return serializer(input as I)
                } catch (e: Throwable) {
                    if (e is CoercingSerializeException) {
                        throw e
                    }

                    throw CoercingSerializeException(
                            "Error trying to serialize ($input) into the scalar type ${type.simpleName}.", e)
                }
            }

            override fun parseValue(output: Any): I {
                try {
                    return deserializer(output)
                } catch (e: Throwable) {
                    if (e is CoercingSerializeException) {
                        throw e
                    }

                    throw CoercingSerializeException(
                            "Error trying to deserialize ($output) for scalar type ${type.simpleName}.", e)
                }
            }

            override fun parseLiteral(ast: Any): I {
                try {
                    return deserializer(Utils.objectFromValue(ast as Value<*>))
                } catch (e: Throwable) {
                    if (e is CoercingSerializeException) {
                        throw e
                    }

                    throw CoercingSerializeException(
                            "Error trying to deserialize the ast ($ast) for scalar type ${type.simpleName}.", e)
                }
            }
        }

        definition.coercing(coercing)

        return definition.build()
    }

    override fun toString(): String {
        return "Scalar(name=${type.simpleName}, description=$description)"
    }
}
