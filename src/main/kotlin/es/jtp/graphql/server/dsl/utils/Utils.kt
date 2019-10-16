package es.jtp.graphql.server.dsl.utils

import es.jtp.graphql.server.dsl.exceptions.*
import graphql.language.*
import graphql.schema.*
import kotlin.reflect.*

internal object Utils {
    /**
     * Creates a [FieldCoordinates] from [KProperty1] using reflection.
     */
    internal fun coordinatesFrom(property: KProperty1<*, *>) =
            FieldCoordinates.coordinates(property.declaringClass().simpleName, property.name)!!

    /**
     * Transforms a name into a directive name, i.e like @directive.
     */
    internal fun directiveNameFrom(name: String) = "${name[0].toLowerCase()}${name.substring(1)}"

    /**
     * Maps a [Value] to an object.
     */
    internal fun objectFromValue(value: Value<*>): Any? = when (value) {
        is NullValue -> null
        is BooleanValue -> value.isValue
        is IntValue -> value.value
        is FloatValue -> value.value
        is StringValue -> value.value
        is ArrayValue -> value.values.map { objectFromValue(value) }
        is EnumValue -> {
            TODO("Check how")
        }
        is ObjectValue -> {
            val obj = mutableMapOf<String, Any?>()

            value.objectFields.forEach {
                obj[it.name] = objectFromValue(it.value)
            }

            obj
        }
        else -> throw GraphQLBuilderUnimplementedException(
                "${Value::class.qualifiedName} type with a value not handled: $value")
    }
}
