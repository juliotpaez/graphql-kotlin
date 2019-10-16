package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.enums.*
import es.jtp.graphql.server.dsl.exceptions.*
import es.jtp.graphql.server.dsl.interfaces.*
import es.jtp.graphql.server.dsl.utils.*
import graphql.schema.*
import kotlin.reflect.*
import kotlin.reflect.full.*


/**
 * Builder for a GraphQL directive.
 * The type name is the lowercase name of type, i.e. for TestDirective -> @testDirective
 */
class DirectiveTypeBuilder<T : Any>(internal val type: KClass<T>) : IGraphQLBuilder, IGraphQLWithDescription {
    override var description: String? = null
    private val locations = mutableSetOf<DirectiveLocation>()
    private val arguments = mutableMapOf<KProperty1<T, *>, ArgumentBuilder<T, *>>()

    init {
        if (type.primaryConstructor == null) {
            throw GraphQLBuilderException("Directives require a Class with a primary constructor.")
        }

        // Add arguments
        for (field in type.memberProperties) {
            val builder = ArgumentBuilder(field)
            arguments[field] = builder
        }
    }

    // METHODS ----------------------------------------------------------------

    /**
     * Sets the locations where the directive can be applied in.
     */
    fun on(vararg locations: DirectiveLocation) {
        this.locations.clear()
        this.locations.addAll(locations)
    }

    /**
     * Sets the locations where the directive can be applied in.
     */
    fun onAnyLocation() {
        locations.clear()
        locations.addAll(DirectiveLocation.values())
    }

    /**
     * Modifies an argument.
     */
    @Suppress("UNCHECKED_CAST")
    fun <V> argument(argument: KProperty1<T, V>, builderFn: ArgumentBuilder<T, V>.() -> Unit) {
        val definitionBuilder =
                arguments[argument] ?: throw GraphQLBuilderException("Cannot modify the argument '${argument.name}'")
        builderFn.invoke(definitionBuilder as ArgumentBuilder<T, V>)
    }

    // TODO include resolver

    /**
     * Builds a [GraphQLDirective].
     */
    override fun build(context: GraphQLBuilderContext): GraphQLDirective {
        val definition = GraphQLDirective.newDirective()

        // Name
        val name = Utils.directiveNameFrom(type.simpleName!!)
        definition.name(name)

        // Description
        if (description != null) {
            definition.description(description!!)
        }

        // Location
        if (locations.isEmpty()) {
            throw GraphQLBuilderException("The directive '$name' must have at least one location")
        }

        locations.forEach {
            definition.validLocation(it.introspectionDirective)
        }

        // Arguments
        arguments.forEach { (_, value) ->
            val argument = value.build(context)
            definition.argument(argument)
        }

        return definition.build()
    }

    override fun toString(): String {
        return "Scalar(name=${type.simpleName}, description=$description, locations=$locations, arguments=$arguments)"
    }
}
