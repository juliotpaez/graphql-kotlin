package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.interfaces.*
import es.jtp.graphql.server.dsl.utils.*
import graphql.schema.*
import kotlin.reflect.*

/**
 * Builder for a GraphQL field definition.
 */
class FieldBuilder<T : Any, V>(val field: KProperty1<T, V>) : IGraphQLWithDescription {
    override var description: String? = null
    private var resolver: ((DataFetchingEnvironment) -> V)? = null
    private var arguments: FieldArgumentsBuilder<*>? = null

    // METHODS ----------------------------------------------------------------

    // TODO include directives - add deprecated

    /**
     * Sets the arguments of the field.
     */
    inline fun <reified R : Any> arguments(noinline builderFn: (FieldArgumentsBuilder<R>.() -> Unit)? = null) =
            arguments(typeClass = R::class, builderFn = builderFn)

    /**
     * Sets the arguments of the field.
     */
    fun <R : Any> arguments(typeClass: KClass<R>, builderFn: (FieldArgumentsBuilder<R>.() -> Unit)? = null) {
        val builder = FieldArgumentsBuilder(typeClass)
        builderFn?.invoke(builder)

        arguments = builder
    }

    /**
     * TODO Resolves a value in the environment to a [T] value.
     */
    fun resolver(fn: (env: DataFetchingEnvironment) -> V) {
        this.resolver = fn
    }

    /**
     * Builds a [GraphQLFieldDefinition].
     */
    internal fun build(context: GraphQLBuilderContext): GraphQLFieldDefinition? {
        val definition = GraphQLFieldDefinition.newFieldDefinition()

        // Name
        definition.name(field.name)

        // Type
        val typeClass = field.returnType.classifier as KClass<*>
        definition.type(GraphQLTypeReference.typeRef(typeClass.simpleName))

        // Description
        if (this.description != null) {
            definition.description(description)
        }

        // Resolver
        if (resolver != null) {
            val dataFetcher = DataFetcher<V>(resolver!!)
            val coordinates = Utils.coordinatesFrom(field)
            context.codeRegistry.dataFetcher(coordinates, dataFetcher)
        }

        // Arguments
        if (arguments != null) {
            arguments!!.arguments.forEach { (_, value) ->
                val argument = value.build(context)
                definition.argument(argument)
            }
        }

        return definition.build()
    }

    override fun toString(): String {
        return "Field(name=${field.name}, type=${(field.returnType.classifier as KClass<*>).simpleName}, description=$description, arguments=$arguments)"
    }
}
