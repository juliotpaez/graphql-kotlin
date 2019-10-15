package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.utils.*
import graphql.schema.*
import kotlin.reflect.*

/**
 * Builder for a GraphQL field definition.
 */
class FieldBuilder<T : Any, V>(val field: KProperty1<T, V>) {
    var description: String? = null
    internal var resolver: ((DataFetchingEnvironment) -> V?)? = null

    // METHODS ----------------------------------------------------------------

    // TODO include arguments
    // TODO include directives - add deprecated
    // TODO include resolver

    /**
     * Resolves the
     */
    fun resolver(fn: (env: DataFetchingEnvironment) -> V?) {
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

        return definition.build()
    }

    override fun toString(): String {
        return "Field(name=${field.name}, type=${(field.returnType.classifier as KClass<*>).simpleName}, description=$description)"
    }
}
