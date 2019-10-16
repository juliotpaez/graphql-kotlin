package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.interfaces.*
import es.jtp.graphql.server.dsl.utils.*
import graphql.schema.*
import kotlin.reflect.*


/**
 * Builder for a GraphQL directive implementation.
 */
class DirectiveImplBuilder<T : Any>(internal val type: KClass<T>) : IGraphQLBuilder {
    private val arguments = mutableMapOf<KProperty1<T, *>, Any?>()

    // METHODS ----------------------------------------------------------------

    /**
     * Sets an argument value.
     */
    fun <V> argument(argument: KProperty1<T, V>, value: V) {
        this.arguments[argument] = value
    }

    /**
     * Builds a [GraphQLDirective].
     */
    override fun build(context: GraphQLBuilderContext): GraphQLDirective {
        val definition = GraphQLDirective.newDirective()

        // Name
        val name = Utils.directiveNameFrom(type.simpleName!!)
        definition.name(name)

        // Arguments
        arguments.forEach { (field, value) ->
            val typeClass = field.returnType.classifier as KClass<*>
            val argument = GraphQLArgument.newArgument().name(field.name)
                    .type(GraphQLTypeReference.typeRef(typeClass.simpleName)).value(value).build()
            definition.argument(argument)
        }

        return definition.build()
    }

    override fun toString(): String {
        return "DirectiveImpl(name=${type.simpleName}, arguments=[${arguments.values.joinToString(", ")}])"
    }
}
