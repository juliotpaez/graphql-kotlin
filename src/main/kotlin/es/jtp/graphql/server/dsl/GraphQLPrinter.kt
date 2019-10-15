package es.jtp.graphql.server.dsl

import es.jtp.graphql.server.dsl.exceptions.*
import es.jtp.graphql.server.dsl.utils.*
import graphql.*
import graphql.schema.*

private val buildInTypes = listOf(Scalars.GraphQLByte, Scalars.GraphQLShort, Scalars.GraphQLInt, Scalars.GraphQLLong,
        Scalars.GraphQLBigInteger, Scalars.GraphQLFloat, Scalars.GraphQLBigDecimal, Scalars.GraphQLChar,
        Scalars.GraphQLString, Scalars.GraphQLBoolean, Scalars.GraphQLID)

/**
 * Prints a [GraphQLSchema].
 */
fun GraphQLSchema.toGraphQLString(includeInternals: Boolean = false) = StringBuilder().apply {
    // Print schema
    let {
        append("schema {\n")

        if (queryType != null) {
            append(PrinterUtils.indent("query: ${queryType.name}"))
            append("\n")
        }

        if (mutationType != null) {
            append(PrinterUtils.indent("mutation: ${mutationType.name}"))
            append("\n")
        }

        if (subscriptionType != null) {
            append(PrinterUtils.indent("subscription: ${subscriptionType.name}"))
            append("\n")
        }

        append("}\n\n")
    }

    // TODO
    //        for (directive in schema.directives) {
    //            append(directive.toGraphQLString())
    //            append("\n\n")
    //        }

    for (type in allTypesAsList) {
        if (!includeInternals && (type.name.startsWith("__") || type in buildInTypes)) {
            continue
        }

        append(type.toGraphQLString())
        append("\n\n")
    }

    removeSuffix("\n\n")
}.toString()

/**
 * Prints a [GraphQLType].
 */
fun GraphQLType.toGraphQLString() = when (this) {
    is GraphQLEnumValueDefinition -> this.toGraphQLString()
    is GraphQLEnumType -> this.toGraphQLString()
    is GraphQLFieldDefinition -> this.toGraphQLString()
    is GraphQLObjectType -> this.toGraphQLString()
    is GraphQLInterfaceType -> this.toGraphQLString()
    is GraphQLUnionType -> this.toGraphQLString()
    is GraphQLScalarType -> this.toGraphQLString()
    else -> throw GraphQLPrinterException("Unsupported type '${this::class}' in printer")
}

/**
 * Prints a [GraphQLEnumValueDefinition].
 */
fun GraphQLEnumValueDefinition.toGraphQLString() = StringBuilder().apply {
    append(printDescription(description))

    append(name)
}.toString()

/**
 * Prints a [GraphQLEnumType].
 */
fun GraphQLEnumType.toGraphQLString() = StringBuilder().apply {
    append(printDescription(description))

    append("enum $name {\n")

    for (value in values) {
        append(PrinterUtils.indent(value.toGraphQLString()))
        append("\n")
    }

    append("}")
}.toString()

/**
 * Prints a [GraphQLFieldDefinition].
 */
fun GraphQLFieldDefinition.toGraphQLString() = StringBuilder().apply {
    append(printDescription(description))

    append("$name: ${type.name}")
}.toString()

/**
 * Prints a [GraphQLObjectType].
 */
fun GraphQLObjectType.toGraphQLString() = StringBuilder().apply {
    append(printDescription(description))

    append("type $name")

    if (interfaces.isNotEmpty()) {
        append(" implements ")

        append(interfaces.joinToString(" & ") { it.name })
    }

    append(" ")

    append(printBlock(fieldDefinitions) {
        it.toGraphQLString()
    })
}.toString()

/**
 * Prints a [GraphQLInterfaceType].
 */
fun GraphQLInterfaceType.toGraphQLString() = StringBuilder().apply {
    append(printDescription(description))

    append("interface $name ")

    append(printBlock(fieldDefinitions) {
        it.toGraphQLString()
    })
}.toString()

/**
 * Prints a [GraphQLUnionType].
 */
fun GraphQLUnionType.toGraphQLString() = StringBuilder().apply {
    append(printDescription(description))

    append("union $name ")

    append(types.joinToString(" | ") { it.name })
}.toString()

/**
 * Prints a [GraphQLScalarType].
 */
fun GraphQLScalarType.toGraphQLString() = StringBuilder().apply {
    append(printDescription(description))

    append("scalar $name")
}.toString()

// AUXILIARY METHODS ------------------------------------------------------

/**
 * Prints the description of an element.
 */
private fun printDescription(description: String?) = if (description != null) {
    if (description.lines().size > 1) {
        "\"\"\"\n$description\n\"\"\"\n"
    } else {
        "\"$description\"\n"
    }
} else {
    ""
}

/**
 * Prints a block of an element.
 */
private fun <T> printBlock(sequence: Iterable<T>, fn: (T) -> String) = StringBuilder().apply {
    append("{\n")

    for (i in sequence) {
        append(PrinterUtils.indent(fn(i)))
        append("\n")
    }

    append("}")
}.toString()

