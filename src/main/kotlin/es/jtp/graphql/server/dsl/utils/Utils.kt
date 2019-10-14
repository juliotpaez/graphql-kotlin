package es.jtp.graphql.server.dsl.utils

import es.jtp.graphql.server.dsl.exceptions.*
import graphql.language.*
import kotlin.reflect.*

internal object Utils {
    /**
     * Creates a new [Description] from a text.
     */
    internal fun descriptionFrom(text: String): Description {
        val sl = SourceLocation(0, text.length)
        return Description(text, sl, text.lines().size > 2)
    }

    /**
     * Creates a [Type] from a [KType] using reflection.
     */
    internal fun typeFromKType(kType: KType): Type<*> {
        if (kType.isMarkedNullable) {
            val classifier = kType.classifier as KClass<*>
            if (classifier.java === List::class.java) {
                // List type
                val listType = ListType.newListType()
                val internalType = typeFromKType(kType.arguments[0].type!!)
                listType.type(internalType)

                return listType.build()!!
            } else {
                // Type name
                val type = TypeName.newTypeName()
                type.name(classifier.simpleName)

                return type.build()!!
            }
        } else {
            val nonNull = NonNullType.newNonNullType()

            val classifier = kType.classifier as KClass<*>
            if (classifier.java === List::class.java) {
                // List type
                val listType = ListType.newListType()
                val internalType = typeFromKType(kType.arguments[0].type!!)
                listType.type(internalType)

                nonNull.type(listType.build()!!)
                return nonNull.build()!!
            } else {
                // Type name
                val type = TypeName.newTypeName()
                type.name(classifier.simpleName)

                nonNull.type(type.build()!!)
                return nonNull.build()!!
            }
        }
    }

    /**
     * Creates a [Type] from a [String] using reflection.
     */
    internal fun typeFromString(typeName: String): Type<*> {
        // Type name
        val type = TypeName.newTypeName()
        type.name(typeName)

        return type.build()!!
    }

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
