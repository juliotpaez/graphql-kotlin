package es.jtp.graphql.server.dsl

import es.jtp.graphql.server.dsl.exceptions.*
import es.jtp.graphql.server.dsl.utils.*
import graphql.*
import graphql.schema.*

private val buildInTypes = listOf(Scalars.GraphQLByte, Scalars.GraphQLShort, Scalars.GraphQLInt, Scalars.GraphQLLong,
        Scalars.GraphQLBigInteger, Scalars.GraphQLFloat, Scalars.GraphQLBigDecimal, Scalars.GraphQLChar,
        Scalars.GraphQLString, Scalars.GraphQLBoolean, Scalars.GraphQLID)

private val buildInDirectives = listOf(Directives.SkipDirective, Directives.IncludeDirective, Directives.DeferDirective)

/**
 * Prints a [GraphQLSchema].
 */
fun GraphQLSchema.toGraphQLString() = StringBuilder().apply {
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

    for (directive in directives) {
        if (directive in buildInDirectives) {
            continue
        }

        append(directive.toGraphQLString())
        append("\n\n")
    }

    for (type in allTypesAsList) {
        if (type.name.startsWith("__") || type in buildInTypes) {
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
    is GraphQLInputObjectField -> this.toGraphQLString()
    is GraphQLObjectType -> this.toGraphQLString()
    is GraphQLInputObjectType -> this.toGraphQLString()
    is GraphQLInterfaceType -> this.toGraphQLString()
    is GraphQLUnionType -> this.toGraphQLString()
    is GraphQLScalarType -> this.toGraphQLString()
    is GraphQLDirective -> this.toGraphQLString()
    is GraphQLArgument -> this.toGraphQLString()
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

    append(name)

    if (arguments.isNotEmpty()) {
        append("(")
        append(arguments.joinToString(", ") { it.toGraphQLString() })
        append(")")
    }

    append(": ${type.name}")
}.toString()

/**
 * Prints a [GraphQLInputObjectField].
 */
fun GraphQLInputObjectField.toGraphQLString() = StringBuilder().apply {
    append(printDescription(description))

    append("$name: ${type.name}")

    if (defaultValue != null) {
        append(" = ")
        append(printValue(defaultValue))
    }
}.toString()

/**
 * Prints a [GraphQLObjectType].
 */
fun GraphQLObjectType.toGraphQLString() = StringBuilder().apply {
    append(printDescription(description))

    append("type $name ")

    if (interfaces.isNotEmpty()) {
        append("implements ")

        append(interfaces.joinToString(" & ") { it.name })
        append(" ")
    }

    if (directives.isNotEmpty()) {
        append(directives.joinToString(" ") { it.toGraphQLValueString() })
        append(" ")
    }

    append(printBlock(fieldDefinitions) {
        it.toGraphQLString()
    })
}.toString()

/**
 * Prints a [GraphQLInputObjectType].
 */
fun GraphQLInputObjectType.toGraphQLString() = StringBuilder().apply {
    append(printDescription(description))

    append("input $name ")

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

    append("union $name = ")

    append(types.joinToString(" | ") { it.name })
}.toString()

/**
 * Prints a [GraphQLScalarType].
 */
fun GraphQLScalarType.toGraphQLString() = StringBuilder().apply {
    append(printDescription(description))

    append("scalar $name")
}.toString()

/**
 * Prints a [GraphQLDirective].
 */
fun GraphQLDirective.toGraphQLString() = StringBuilder().apply {
    append(printDescription(description))

    append("directive @$name")

    if (arguments.isNotEmpty()) {
        append("(")
        append(arguments.joinToString(", ") { it.toGraphQLString() })
        append(")")
    }

    append(" on ")
    append(validLocations().joinToString(" | ") { it.name })
}.toString()


/**
 * Prints a [GraphQLDirective] as value.
 */
fun GraphQLDirective.toGraphQLValueString() = StringBuilder().apply {
    append("@$name")

    if (arguments.isNotEmpty()) {
        append("(")
        append(arguments.joinToString(", ") { it.toGraphQLValueString() })
        append(")")
    }
}.toString()

/**
 * Prints a [GraphQLArgument].
 */
fun GraphQLArgument.toGraphQLString() = StringBuilder().apply {
    append(printDescription(description))

    append("$name: ${type.name}")

    if (defaultValue != null) {
        append(" = ")
        append(printValue(defaultValue))
    }
}.toString()

/**
 * Prints a [GraphQLArgument] as value.
 */
fun GraphQLArgument.toGraphQLValueString() = StringBuilder().apply {
    append("$name: ${printValue(value)}")
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


/**
 * Prints a value.
 */
private fun printValue(value: Any) = StringBuilder().apply {
    if (value is String) {
        val safeValue = value.replace("\\", "\\\\").replace("\n", "\\n").replace("\"", "\\\"")
        append("\"$safeValue\"")
    } else {
        append(value)
    }
}.toString()

