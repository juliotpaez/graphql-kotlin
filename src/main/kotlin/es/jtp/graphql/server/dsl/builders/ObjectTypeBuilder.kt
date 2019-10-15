package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.exceptions.*
import es.jtp.graphql.server.dsl.interfaces.*
import graphql.schema.*
import kotlin.reflect.*
import kotlin.reflect.full.*


/**
 * Builder for a GraphQL object type.
 */
class ObjectTypeBuilder<T : Any>(val type: KClass<T>) : ITypeBuilder {
    var description: String? = null
    internal val definitionBuilders = mutableMapOf<KProperty1<T, *>, FieldBuilder<T, *>>()
    internal val implements = mutableSetOf<KClass<*>>()

    init {
        for (field in type.memberProperties) {
            val builder = FieldBuilder(field)
            definitionBuilders[field] = builder
        }
    }

    // METHODS ----------------------------------------------------------------

    // TODO include directives

    /**
     * Sets a Implementation of two types.
     */
    inline fun <reified T1> implementOne() {
        implement(T1::class)
    }

    /**
     * Sets a Implementation of two types.
     */
    inline fun <reified T1, reified T2> implement() {
        implement(T1::class, T2::class)
    }

    /**
     * Sets a Implementation of three types.
     */
    inline fun <reified T1, reified T2, reified T3> implementThree() {
        implement(T1::class, T2::class, T3::class)
    }

    /**
     * Sets a Implementation of four types.
     */
    inline fun <reified T1, reified T2, reified T3, reified T4> implementFour() {
        implement(T1::class, T2::class, T3::class, T4::class)
    }

    /**
     * Sets the types for the Implementation.
     */
    fun implement(vararg unionTypes: KClass<*>) {
        implements.clear()
        unionTypes.forEach {
            implements.add(it)
        }
    }

    /**
     * Modifies a field of a type.
     */
    @Suppress("UNCHECKED_CAST")
    fun <V> field(fieldProperty: KProperty1<T, V>, builderFn: FieldBuilder<T, V>.() -> Unit) {
        val definitionBuilder = definitionBuilders[fieldProperty] ?: throw GraphQLBuilderException(
                "Cannot modify the field '${fieldProperty.name}'")
        builderFn.invoke(definitionBuilder as FieldBuilder<T, V>)
    }

    /**
     * Builds a [GraphQLObjectType].
     */
    override fun build(context: GraphQLBuilderContext): GraphQLObjectType {
        val definition = GraphQLObjectType.newObject()

        // Name
        definition.name(type.simpleName)

        // Description
        if (this.description != null) {
            definition.description(description)
        }

        // Fields
        for (definitionBuilder in definitionBuilders) {
            val fieldDefinition = definitionBuilder.value.build(context)
            definition.field(fieldDefinition)
        }

        // Implements
        for (implement in implements) {
            definition.withInterface(GraphQLTypeReference.typeRef(implement.simpleName))
        }

        return definition.build()
    }

    override fun toString(): String {
        return "Object(name=${type.simpleName}, description=$description, fields=[${definitionBuilders.values.joinToString(
                ", ")}]})"
    }
}
