package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.exceptions.*
import es.jtp.graphql.server.dsl.interfaces.*
import graphql.schema.*
import kotlin.reflect.*

/**
 * Builder for a GraphQL schema.
 */
class GraphQLSchemaBuilder {
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
    inline fun <reified T : Any> type(noinline builderFn: (ObjectTypeBuilder<T>.() -> Unit)? = null) =
            type(T::class, builderFn)

    /**
     * Defines a new object type.
     */
    fun <T : Any> type(typeClass: KClass<T>, builderFn: (ObjectTypeBuilder<T>.() -> Unit)? = null) {
        val builder = ObjectTypeBuilder(typeClass)
        builderFn?.invoke(builder)

        if (typeClass.simpleName in typeDefinitions) {
            throw GraphQLBuilderException(
                    "The type '${typeClass.simpleName}' is already defined [previous: ${typeDefinitions[typeClass.simpleName]}, current: $builder]")
        }

        typeDefinitions[typeClass.simpleName!!] = builder
    }

    /**
     * Defines a new interface type.
     */
    inline fun <reified T : Any> interfaceType(noinline builderFn: (InterfaceTypeBuilder<T>.() -> Unit)? = null) =
            interfaceType(T::class, builderFn)

    /**
     * Defines a new interface type.
     */
    fun <T : Any> interfaceType(typeClass: KClass<T>, builderFn: (InterfaceTypeBuilder<T>.() -> Unit)? = null) {
        val builder = InterfaceTypeBuilder(typeClass)
        builderFn?.invoke(builder)

        if (typeClass.simpleName in typeDefinitions) {
            throw GraphQLBuilderException(
                    "The type '${typeClass.simpleName}' is already defined [previous: ${typeDefinitions[typeClass.simpleName]}, current: $builder]")
        }

        typeDefinitions[typeClass.simpleName!!] = builder
    }

    /**
     * Defines a new union type.
     */
    inline fun <reified T : Any> union(noinline builderFn: (UnionTypeBuilder<T>.() -> Unit)? = null) =
            union(T::class, builderFn)

    /**
     * Defines a new union type.
     */
    fun <T : Any> union(typeClass: KClass<T>, builderFn: (UnionTypeBuilder<T>.() -> Unit)? = null) {
        val builder = UnionTypeBuilder(typeClass)
        builderFn?.invoke(builder)

        if (typeClass.simpleName in typeDefinitions) {
            throw GraphQLBuilderException(
                    "The type '${typeClass.simpleName}' is already defined [previous: ${typeDefinitions[typeClass.simpleName]}, current: $builder]")
        }

        typeDefinitions[typeClass.simpleName!!] = builder
    }

    /**
     * Defines a new enum type.
     */
    inline fun <reified T : Enum<T>> enum(noinline builderFn: (EnumTypeBuilder<T>.() -> Unit)? = null) =
            enum(T::class, builderFn)

    /**
     * Defines a new enum type.
     */
    fun <T : Enum<T>> enum(typeClass: KClass<T>, builderFn: (EnumTypeBuilder<T>.() -> Unit)? = null) {
        val builder = EnumTypeBuilder(typeClass)
        builderFn?.invoke(builder)

        if (typeClass.simpleName in typeDefinitions) {
            throw GraphQLBuilderException(
                    "The type '${typeClass.simpleName}' is already defined [previous: ${typeDefinitions[typeClass.simpleName]}, current: $builder]")
        }

        typeDefinitions[typeClass.simpleName!!] = builder
    }

    // TODO add enum
    // TODO add input object

    /**
     * Builds a [GraphQLSchema].
     */
    fun build(): GraphQLSchema {
        val definition = GraphQLSchema.newSchema()
        val codeRegistry = GraphQLCodeRegistry.newCodeRegistry()
        val context = GraphQLBuilderContext(definition, codeRegistry)

        // Types
        for (typeDefinition in typeDefinitions) {
            val type = typeDefinition.value.build(context)
            definition.additionalType(type)
            context.types[type.name] = type
        }

        // Schema
        if (schema == null) {
            throw GraphQLBuilderException("At least the query type of the schema must be defined")
        }

        return schema!!.build(context)
    }

    override fun toString(): String {
        return "GraphQL(schema=$schema, typeDefinitions=[${typeDefinitions.values.joinToString(", ")}])"
    }
}
