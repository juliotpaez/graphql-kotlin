package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.exceptions.*
import es.jtp.graphql.server.dsl.interfaces.*
import graphql.schema.*
import kotlin.reflect.*
import kotlin.reflect.full.*


/**
 * Builder for a GraphQL object type.
 */
class ObjectTypeBuilder<T : Any>(val type: KClass<T>) : IGraphQLBuilder, IGraphQLWithDescription {
    override var description: String? = null
    private val definitionBuilders = mutableMapOf<KProperty1<T, *>, FieldBuilder<T, *>>()
    private val implements = mutableSetOf<KClass<*>>()
    private val directives = mutableMapOf<KClass<*>, DirectiveImplBuilder<*>>()

    init {
        for (field in type.memberProperties) {
            val builder = FieldBuilder(field)
            definitionBuilders[field] = builder
        }
    }

    // METHODS ----------------------------------------------------------------

    /**
     * Adds a new directive implementation.
     */
    inline fun <reified R : Any> directive(noinline builderFn: (DirectiveImplBuilder<R>.() -> Unit)? = null) =
            directive(typeClass = R::class, builderFn = builderFn)

    /**
     * Adds a new directive implementation.
     */
    fun <R : Any> directive(typeClass: KClass<R>, builderFn: (DirectiveImplBuilder<R>.() -> Unit)? = null) {
        val builder = DirectiveImplBuilder(typeClass)
        builderFn?.invoke(builder)

        directives[typeClass] = builder
    }

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
        for (definitionBuilder in definitionBuilders.values) {
            val fieldDefinition = definitionBuilder.build(context)
            definition.field(fieldDefinition)
        }

        // Implements
        for (implement in implements) {
            definition.withInterface(GraphQLTypeReference.typeRef(implement.simpleName))
        }

        // Directives
        for ((_, value) in directives) {
            val directive = value.build(context)
            definition.withDirective(directive)
        }

        return definition.build()
    }

    override fun toString(): String {
        return "Object(name=${type.simpleName}, description=$description, fields=[${definitionBuilders.values.joinToString(
                ", ")}], implements=[${implements.joinToString(", ")}], implements=[${directives.values.joinToString(
                ", ")}])"
    }
}
