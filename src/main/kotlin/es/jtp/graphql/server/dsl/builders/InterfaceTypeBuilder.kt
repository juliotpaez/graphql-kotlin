package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.exceptions.*
import es.jtp.graphql.server.dsl.interfaces.*
import graphql.schema.*
import kotlin.reflect.*
import kotlin.reflect.full.*


/**
 * Builder for a GraphQL interface type.
 */
@Suppress("UNCHECKED_CAST")
class InterfaceTypeBuilder<T : Any>(val type: KClass<T>) : ITypeBuilder {
    var description: String? = null
    internal val definitionBuilders = mutableMapOf<KProperty1<T, *>, FieldBuilder<T, *>>()

    init {
        for (field in type.memberProperties) {
            val builder = FieldBuilder(field)
            definitionBuilders[field] = builder
        }
    }

    // METHODS ----------------------------------------------------------------

    // TODO include directives
    // TODO include resolver

    /**
     * Modifies a field of a type.
     */
    fun <V> field(fieldProperty: KProperty1<T, V>, builderFn: FieldBuilder<T, V>.() -> Unit) {
        val definitionBuilder = definitionBuilders[fieldProperty] ?: throw GraphQLBuilderException(
                "Cannot modify the field '${fieldProperty.name}'")
        builderFn.invoke(definitionBuilder as FieldBuilder<T, V>)
    }

    /**
     * Builds a [GraphQLInterfaceType].
     */
    override fun build(context: GraphQLBuilderContext): GraphQLInterfaceType {
        val definition = GraphQLInterfaceType.newInterface()

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

        // Resolver
        val result = definition.build()

        context.codeRegistry.typeResolver(result) { env ->
            val obj: Any = env.getObject()
            val clazz = obj::class

            if (clazz.isSubclassOf(type)) {
                return@typeResolver env.schema.getObjectType(clazz.simpleName!!)
            }

            null
        }

        return definition.build()
    }

    override fun toString(): String {
        return "Interface(name=${type.simpleName}, description=$description, fields=[${definitionBuilders.values.joinToString(
                ", ")}]})"
    }
}
