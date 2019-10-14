package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.exceptions.*
import es.jtp.graphql.server.dsl.interfaces.*
import graphql.schema.idl.*
import kotlin.reflect.*

/**
 * Builder for a GraphQL schema.
 */
class GraphQLBuilder {
    internal var schema: SchemaBuilder? = null
    internal val typeDefinitions = mutableMapOf<String, ITypeBuilder>()

    // METHODS ----------------------------------------------------------------

    /**
     * Defines the schema of the GraphQL.
     */
    fun schema(builderFn: SchemaBuilder.() -> Unit) {
        val builder = SchemaBuilder()
        builderFn.invoke(builder)

        this.schema = builder
    }

    /**
     * Defines a new scalar type.
     */
    inline fun <reified I : Any> scalar(noinline builderFn: ScalarTypeBuilder<I>.() -> Unit) =
            scalar(I::class, builderFn)

    /**
     * Defines a new scalar type.
     */
    fun <I : Any> scalar(typeClass: KClass<I>, builderFn: ScalarTypeBuilder<I>.() -> Unit) {
        val builder = ScalarTypeBuilder<I>(typeClass)
        builderFn.invoke(builder)

        if (typeClass.simpleName in typeDefinitions) {
            throw GraphQLBuilderException(
                    "The type '${typeClass.simpleName}' is already defined [previous: ${typeDefinitions[typeClass.simpleName]}, current: $builder]")
        }

        typeDefinitions[typeClass.simpleName!!] = builder
    }

    /**
     * Defines a new object type.
     */
    inline fun <reified T : Any> type(noinline builderFn: ObjectTypeBuilder<T>.() -> Unit) = type(T::class, builderFn)

    /**
     * Defines a new object type.
     */
    fun <T : Any> type(typeClass: KClass<T>, builderFn: ObjectTypeBuilder<T>.() -> Unit) {
        val builder = ObjectTypeBuilder(typeClass)
        builderFn.invoke(builder)

        if (typeClass.simpleName in typeDefinitions) {
            throw GraphQLBuilderException(
                    "The type '${typeClass.simpleName}' is already defined [previous: ${typeDefinitions[typeClass.simpleName]}, current: $builder]")
        }

        typeDefinitions[typeClass.simpleName!!] = builder
    }

    /**
     * Defines a new interface type.
     */
    inline fun <reified T : Any> interfaceType(noinline builderFn: InterfaceTypeBuilder<T>.() -> Unit) =
            interfaceType(T::class, builderFn)

    /**
     * Defines a new interface type.
     */
    fun <T : Any> interfaceType(typeClass: KClass<T>, builderFn: InterfaceTypeBuilder<T>.() -> Unit) {
        val builder = InterfaceTypeBuilder(typeClass)
        builderFn.invoke(builder)

        if (typeClass.simpleName in typeDefinitions) {
            throw GraphQLBuilderException(
                    "The type '${typeClass.simpleName}' is already defined [previous: ${typeDefinitions[typeClass.simpleName]}, current: $builder]")
        }

        typeDefinitions[typeClass.simpleName!!] = builder
    }

    /**
     * Defines a new union type.
     */
    fun union(name: String, builderFn: UnionTypeBuilder.() -> Unit) {
        val builder = UnionTypeBuilder(name)
        builderFn.invoke(builder)

        if (name in typeDefinitions) {
            throw GraphQLBuilderException(
                    "The type '$name' is already defined [previous: ${typeDefinitions[name]}, current: $builder]")
        }

        typeDefinitions[name] = builder
    }

    // TODO add enum
    // TODO add input object

    /**
     * Builds a [TypeDefinitionRegistry].
     */
    fun build(context: GraphQLBuilderContext): TypeDefinitionRegistry {
        val definition = TypeDefinitionRegistry()

        // Schema
        if (schema != null) {
            val schema = schema!!.build(context)
            definition.add(schema)
        }

        // Types
        for (typeDefinition in typeDefinitions) {
            val type = typeDefinition.value.build(context)
            definition.add(type)
        }

        return definition
    }

    /**
     * Prints the definition as a GraphQL schema.
     */
    fun toGraphQLString() = StringBuilder().apply {
        if (schema != null) {
            append(schema!!.toGraphQLString())
            append("\n\n")
        }

        for (typeDefinition in typeDefinitions.values) {
            append(typeDefinition.toGraphQLString())
            append("\n\n")
        }
    }.toString()

    override fun toString(): String {
        return "GraphQL(schema=$schema, typeDefinitions=[${typeDefinitions.values.joinToString(", ")}])"
    }
}
