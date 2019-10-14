package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.exceptions.*
import es.jtp.graphql.server.dsl.interfaces.*
import es.jtp.graphql.server.dsl.utils.*
import graphql.language.*
import graphql.schema.*
import graphql.schema.idl.*
import kotlin.reflect.*
import kotlin.reflect.full.*


/**
 * Builder for a GraphQL interface type.
 */
@Suppress("UNCHECKED_CAST")
class InterfaceTypeBuilder<T : Any>(val type: KClass<T>) : ITypeBuilder {
    var description: String? = null
    internal val definitionBuilders = mutableMapOf<KProperty1<T, *>, FieldBuilder<T>>()

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
    fun field(fieldProperty: KProperty1<T, *>, builderFn: FieldBuilder<T>.() -> Unit) {
        val definitionBuilder = definitionBuilders[fieldProperty] ?: throw GraphQLBuilderException(
                "Cannot modify the field '${fieldProperty.name}'")
        builderFn.invoke(definitionBuilder)
    }

    /**
     * Builds a [InterfaceTypeDefinition].
     */
    override fun build(context: GraphQLBuilderContext): InterfaceTypeDefinition {
        val definition = InterfaceTypeDefinition.newInterfaceTypeDefinition()

        // Name
        definition.name(type.simpleName)

        // Description
        if (this.description != null) {
            val description = Utils.descriptionFrom(this.description!!)
            definition.description(description)
        }

        // Fields
        for (definitionBuilder in definitionBuilders) {
            val fieldDefinition = definitionBuilder.value.build(context)
            definition.definition(fieldDefinition)
        }

        // Resolver
        val resolver = TypeResolver { env ->
            val obj: Any = env.getObject()
            val className = obj::class.simpleName!!
            env.schema.getObjectType(className)
        }
        val runtimeWiring = TypeRuntimeWiring.newTypeWiring(type.simpleName).typeResolver(resolver)
        context.runtimeWiringBuilder.type(runtimeWiring.build())

        return definition.build()
    }

    /**
     * Prints the definition as a GraphQL schema.
     */
    override fun toGraphQLString() = StringBuilder().apply {
        if (description != null) {
            append("\"\"\"\n$description\n\"\"\"\n")
        }

        append("interface ${type.simpleName} {\n")

        for (definitionBuilder in definitionBuilders.values) {
            append(PrinterUtils.indent(definitionBuilder.toGraphQLString()))
            append("\n")
        }

        append("}")
    }.toString()

    override fun toString(): String {
        return "Interface(name=${type.simpleName}, description=$description, fields=[${definitionBuilders.values.joinToString(
                ", ")}]})"
    }
}
