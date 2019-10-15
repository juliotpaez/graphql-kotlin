package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.exceptions.*
import es.jtp.graphql.server.dsl.interfaces.*
import graphql.schema.*
import kotlin.reflect.*
import kotlin.reflect.full.*

/**
 * Builder for a GraphQL union type.
 */
@Suppress("UNCHECKED_CAST")
class UnionTypeBuilder<T : Any>(val type: KClass<T>) : ITypeBuilder {
    var description: String? = null
    internal val types = mutableSetOf<KClass<*>>()

    // METHODS ----------------------------------------------------------------

    // TODO include directives

    /**
     * Sets a Union of two types.
     */
    inline fun <reified T1 : T, reified T2 : T> ofTwo() {
        of(T1::class, T2::class)
    }

    /**
     * Sets a Union of three types.
     */
    inline fun <reified T1 : T, reified T2 : T, reified T3 : T> ofThree() {
        of(T1::class, T2::class, T3::class)
    }

    /**
     * Sets a Union of four types.
     */
    inline fun <reified T1 : T, reified T2 : T, reified T3 : T, reified T4 : T> ofFour() {
        of(T1::class, T2::class, T3::class, T4::class)
    }

    /**
     * Sets the types of the union.
     */
    fun of(vararg unionTypes: KClass<*>) {
        types.clear()
        unionTypes.forEach {
            if (!it.isSubclassOf(type)) {
                throw GraphQLBuilderException("The type $it is not a subclass of $type")
            }
            types.add(it)
        }
    }

    /**
     * Builds a [GraphQLUnionType].
     */
    override fun build(context: GraphQLBuilderContext): GraphQLUnionType {
        val definition = GraphQLUnionType.newUnionType()

        // Name
        definition.name(type.simpleName)

        // Description
        if (this.description != null) {
            definition.description(description)
        }

        // Member types
        for (type in types) {
            definition.possibleType(GraphQLTypeReference.typeRef(type.simpleName))
        }

        // Resolver
        val result = definition.build()

        context.codeRegistry.typeResolver(result) { env ->
            val obj: Any = env.getObject()
            val clazz = obj::class

            if (clazz in types) {
                return@typeResolver env.schema.getObjectType(clazz.simpleName!!)
            }

            null
        }

        return result
    }

    override fun toString(): String {
        return "Union(name=${type.simpleName}, description=$description, types=[${types.joinToString(
                " | ") { it.simpleName!! }}])"
    }
}
