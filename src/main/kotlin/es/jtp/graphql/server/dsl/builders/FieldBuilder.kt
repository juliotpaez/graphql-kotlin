package es.jtp.graphql.server.dsl.builders

import es.jtp.graphql.server.dsl.utils.*
import graphql.language.*
import kotlin.reflect.*

/**
 * Builder for a GraphQL field definition.
 */
class FieldBuilder<T : Any>(val field: KProperty1<T, *>) {
    var description: String? = null

    // METHODS ----------------------------------------------------------------

    // TODO include arguments
    // TODO include directives - add deprecated
    // TODO include resolver

    /**
     * Builds a [FieldDefinition].
     */
    internal fun build(context: GraphQLBuilderContext): FieldDefinition {
        val definition = FieldDefinition.newFieldDefinition()

        // Name
        definition.name(field.name)

        // Type
        val fieldType = Utils.typeFromKType(field.returnType)
        definition.type(fieldType)

        // Description
        if (this.description != null) {
            val description = Utils.descriptionFrom(this.description!!)
            definition.description(description)
        }

        return definition.build()
    }

    /**
     * Prints the definition as a GraphQL schema.
     */
    fun toGraphQLString() = StringBuilder().apply {
        if (description != null) {
            append("\"\"\"\n$description\n\"\"\"\n")
        }

        append("${field.name}: ${PrinterUtils.printType(field.returnType)}")
    }.toString()

    override fun toString(): String {
        return "Field(name=${field.name}, type=${PrinterUtils.printType(field.returnType)}, description=$description)"
    }
}
