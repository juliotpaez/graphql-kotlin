package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.exceptions.*
import es.jtp.graphql.server.dsl.interfaces.*
import es.jtp.graphql.server.dsl.utils.*
import graphql.language.*
import kotlin.reflect.*

/**
 * Builder for a GraphQL union type.
 */
@Suppress("UNCHECKED_CAST")
class UnionTypeBuilder(val name: String) : ITypeBuilder {
    var description: String? = null
    internal val types = mutableSetOf<KClass<*>>()

    // METHODS ----------------------------------------------------------------

    // TODO include directives

    /**
     * Sets a Union of two types.
     */
    inline fun <reified T1, reified T2> ofTwo() {
        of(T1::class, T2::class)
    }

    /**
     * Sets a Union of three types.
     */
    inline fun <reified T1, reified T2, reified T3> ofThree() {
        of(T1::class, T2::class, T3::class)
    }

    /**
     * Sets a Union of four types.
     */
    inline fun <reified T1, reified T2, reified T3, reified T4> ofFour() {
        of(T1::class, T2::class, T3::class, T4::class)
    }

    /**
     * Sets the types of the union.
     */
    fun of(vararg unionTypes: KClass<*>) {
        types.clear()
        unionTypes.forEach {
            types.add(it)
        }
    }

    /**
     * Builds a [ObjectTypeDefinition].
     */
    override fun build(context: GraphQLBuilderContext): UnionTypeDefinition {
        val definition = UnionTypeDefinition.newUnionTypeDefinition()

        // Name
        definition.name(name)

        // Description
        if (this.description != null) {
            val description = Utils.descriptionFrom(this.description!!)
            definition.description(description)
        }

        // Member types
        for (type in types) {
            val fieldType = Utils.typeFromString(type.simpleName!!)
            definition.memberType(fieldType)
        }

        return definition.build()
    }

    /**
     * Prints the definition as a GraphQL schema.
     */
    override fun toGraphQLString() = StringBuilder().apply {
        if (description != null) {
            append("\"\"\"\n$description\n\"\"\"\n")
        }

        append("union $name ")

        if (types.isEmpty()) {
            throw GraphQLBuilderException("The union require at least one type")
        }

        append(types.joinToString(" | ") { it.simpleName!! })
    }.toString()

    override fun toString(): String {
        return "Union(name=$name, description=$description, types=[${types.joinToString(" | ") { it.simpleName!! }}])"
    }
}
